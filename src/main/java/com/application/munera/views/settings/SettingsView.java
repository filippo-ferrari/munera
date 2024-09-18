package com.application.munera.views.settings;

import com.application.munera.data.User;
import com.application.munera.services.UserService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Settings")
@PermitAll
@Uses(Icon.class)
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    private TextField firstName;
    private TextField lastName;
    private PasswordField password;
    private NumberField monthlyIncome;
    private EmailField email;
    private final BeanValidationBinder<User> binder;
    private final Button save = new Button("Save");
    private final User loggedInUser;

    @Autowired
    public SettingsView(UserService userService) {
        this.userService = userService;
        createForm();
        loggedInUser = userService.getLoggedInUser();

        // Bind fields. This is where you'd define e.g. validation rules
        binder = new BeanValidationBinder<>(User.class);
        binder.bindInstanceFields(this);
        binder.forField(firstName)
                .asRequired("First name is required")
                .bind(User::getFirstName, User::setFirstName);
        binder.forField(lastName)
                .asRequired("Last name is required")
                .bind(User::getLastName, User::setLastName);
        binder.forField(password)
                .asRequired("Password is required")
                .bind(User::getPassword, User::setPassword);
        binder.forField(monthlyIncome)
                .withNullRepresentation(0.0) // 0.0 if the field is empty
                .withValidator(value -> value != null, "Must be a valid number")
                .bind(User::getMonthlyIncome, User::setMonthlyIncome);

        save.addClickListener(e -> {
            try {
                binder.writeBean(this.loggedInUser);
                this.saveUserData();
                Notification.show("User details updated successfully");
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the user. Check again that all values are valid");
            }
        });
    }

    private void createForm() {
        FormLayout formLayout = new FormLayout();

        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        password = new PasswordField("Password");
        email = new EmailField("Email");
        monthlyIncome = new NumberField("Monthly Income");

        formLayout.add(firstName, lastName, password, email, monthlyIncome);

        add(formLayout, this.save);
    }

    private void saveUserData() {
        loggedInUser.setFirstName(firstName.getValue());
        loggedInUser.setLastName(lastName.getValue());
        loggedInUser.setEmail(email.getValue());
        loggedInUser.setPassword(password.getValue());
        loggedInUser.setMonthlyIncome(monthlyIncome.getValue());
        userService.saveOrUpdateUserAndConnectedPerson(loggedInUser);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final var getLoggedInUser = userService.getLoggedInUser();
        firstName.setValue(getLoggedInUser.getFirstName());
        lastName.setValue(getLoggedInUser.getLastName());
        password.setValue(getLoggedInUser.getPassword());
        email.setValue(getLoggedInUser.getEmail());
        monthlyIncome.setValue(getLoggedInUser.getMonthlyIncome());
    }
}

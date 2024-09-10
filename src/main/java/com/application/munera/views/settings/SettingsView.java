package com.application.munera.views.settings;

import com.application.munera.services.UserService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@PageTitle("Settings")
@PermitAll
@Uses(Icon.class)
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    private TextField firstNameField;
    private TextField lastNameField;
    private PasswordField passwordField;
    private TextField monthlyIncomeField;

    @Autowired
    public SettingsView(UserService userService) {
        this.userService = userService;

        createForm();
    }

    private void createForm() {
        FormLayout formLayout = new FormLayout();

        firstNameField = new TextField("First Name");
        lastNameField = new TextField("Last Name");
        passwordField = new PasswordField("Password");
        monthlyIncomeField = new TextField("Monthly Income");

        formLayout.add(firstNameField, lastNameField, passwordField, monthlyIncomeField);

        Button saveButton = new Button("Save", click -> saveUserData());

        add(formLayout, saveButton);
    }

    private void saveUserData() {
        final var loggedInUser = userService.getLoggedInUser().orElseThrow(() -> new UsernameNotFoundException("User not found"));

        loggedInUser.setFirstName(firstNameField.getValue());
        loggedInUser.setLastName(lastNameField.getValue());

        // Only update the password if it's not empty
        if (!passwordField.isEmpty()) {
            loggedInUser.setPassword(passwordField.getValue());
        }

        // Handle saving the monthly income separately if needed
        // For now, we'll just print it out
        String monthlyIncome = monthlyIncomeField.getValue();
        System.out.println("Monthly Income: " + monthlyIncome);

        userService.updateUser(loggedInUser);
        Notification.show("User details updated successfully");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final var loggedInUser = userService.getLoggedInUser().orElseThrow(() -> new UsernameNotFoundException("User not found"));
        firstNameField.setValue(loggedInUser.getFirstName());
        lastNameField.setValue(loggedInUser.getLastName());
        monthlyIncomeField.setValue(""); //TODO: implement monthly income
    }
}

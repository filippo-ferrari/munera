package com.application.munera.views.users;

import com.application.munera.data.User;
import com.application.munera.services.UserService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;


@PageTitle("Users")
@RolesAllowed("ADMIN")
@Route(value = "users/:userID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class UsersView extends Div implements BeforeEnterObserver {

    private static final String USER_ID = "userID";
    private static final String USER_EDIT_ROUTE_TEMPLATE = "users/%s/edit";

    private final Grid<User> grid = new Grid<>();

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<User> binder;

    private User user;
    private final UserService userService;
    private TextField firstName;
    private TextField lastName;
    private TextField username;
    private TextField roles;
    private PasswordField password;
    private EmailField email;

    public UsersView(UserService userService) {
        this.userService = userService;
        addClassNames("expenses-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn(User::getFirstName).setHeader("First name").setSortable(true);
        grid.addColumn(User::getLastName).setHeader("Last name").setSortable(true);
        grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);
        grid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(User::getRoles).setHeader("Roles").setSortable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.setItems(this.userService.findAll());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) UI.getCurrent().navigate(String.format(USER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            else {
                clearForm();
                UI.getCurrent().navigate(UsersView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(User.class);
        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        binder.forField(firstName)
                .asRequired("First name is required")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lastName)
                .asRequired("Last name is required")
                .bind(User::getLastName, User::setLastName);

        binder.forField(username)
                .asRequired("Username is required")
                .bind(User::getUsername, User::setUsername);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.user == null) this.user = new User();
                binder.writeBean(this.user);
                this.userService.saveOrUpdateUserAndConnectedPerson(this.user);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(UsersView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the user. Check again that all values are valid");
            }
        });

        delete.addClickListener(e -> {
            try {
                if (this.user == null) throw new IllegalStateException("The user is null!");
                userService.delete(this.user);
                clearForm();
                refreshGrid();
                Notification.show("Data delete");
                UI.getCurrent().navigate(UsersView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> userId = event.getRouteParameters().get(USER_ID).map(Long::parseLong);
        if (userId.isPresent()) {
            Optional<User> userFromBackend = this.userService.findById(userId.get());
            if (userFromBackend.isPresent()) populateForm(userFromBackend.get());
            else {
                Notification.show(
                        String.format("The requested user was not found, ID = %s", userId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid(); // when a row is selected but the data is no longer available refresh grid
                event.forwardTo(UsersView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        username = new TextField("Username");
        password = new PasswordField("Password");
        roles = new TextField("Roles");
        email = new EmailField("Email");

        // We set the maximum parallel columns to 1
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        formLayout.add(firstName, lastName, username, password, email, roles);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(User value) {
        this.user = value;
        binder.readBean(this.user);

    }
}

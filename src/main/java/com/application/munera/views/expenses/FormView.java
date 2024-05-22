package com.application.munera.views.expenses;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;

import java.awt.*;

@Route("form")
public class FormView extends Div {

    public FormView() {
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm password");

        FormLayout formLayout = new FormLayout();
        formLayout.add(password);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep("500px", 2));
        // Stretch the username field over 2 columns
        formLayout.setColspan(password, 2);

        add(formLayout);
    }

}
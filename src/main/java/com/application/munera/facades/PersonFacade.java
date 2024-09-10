package com.application.munera.facades;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.services.ExpenseService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonFacade {

    private final ExpenseService expenseService;

    public PersonFacade(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    public void setDebtPaid(Person person, TreeGrid<Object> grid) {
        try {
            List<Expense> expenses = expenseService.findExpensesWherePayer(person).stream().toList();
            for (Expense expense : expenses) {
                expense.setIsPaid(true);
                expenseService.update(expense);
            }
            Notification.show("All expenses marked as paid for " + person.getFirstName() + " " + person.getLastName());
            grid.select(null);
            grid.getDataProvider().refreshAll();
        } catch (Exception e) {
            Notification n = Notification.show("Error marking expenses as paid: " + e.getMessage());
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void setCreditPaid(Person person, TreeGrid<Object> grid) {
        try {
            List<Expense> expenses = expenseService.findExpensesWhereBeneficiary(person).stream().toList();
            for (Expense expense : expenses) {
                expense.setIsPaid(true);
                expenseService.update(expense);
            }
            Notification.show("All expenses marked as paid for " + person.getFirstName() + " " + person.getLastName());
            grid.select(null);
            grid.getDataProvider().refreshAll();
        } catch (Exception e) {
            Notification n = Notification.show("Error marking expenses as paid: " + e.getMessage());
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
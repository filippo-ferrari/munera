package com.application.munera.facades;

import com.application.munera.data.Expense;
import com.application.munera.services.ExpenseService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.treegrid.TreeGrid;
import org.springframework.stereotype.Component;

@Component
public class ExpenseFacade {
    public final ExpenseService expenseService;

    public ExpenseFacade(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public void setExpensePaid(Expense expense, TreeGrid<Object> grid) {
        expense.setIsPaid(true);
        this.expenseService.update(expense);
        Notification.show("Expense " + expense.getName() + " set as paid" );
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }
}
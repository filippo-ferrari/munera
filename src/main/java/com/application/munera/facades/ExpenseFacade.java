package com.application.munera.facades;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.services.ExpenseService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.treegrid.TreeGrid;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpenseFacade {
    public final ExpenseService expenseService;

    public ExpenseFacade(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Sets an expense as paid
     * @param expense the expense to set as paid
     * @param grid the grid reference to update
     * @param userId the id of the user related to the expense
     */
    public void setExpensePaid(Expense expense, TreeGrid<Object> grid, Long userId) {
        expense.setIsPaid(true);
        this.expenseService.update(expense, userId);
        Notification.show("Expense " + expense.getName() + " set as paid" );
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    /**
     * Finds all expenses related to a person, both where the person is a payer and a beneficiary.
     * @param person the person of the expenses
     * @return the list of expenses found
     */
    public List<Expense> findExpensesByPerson(final Person person) {
        return this.expenseService.findExpensesByPerson(person);
    }
}
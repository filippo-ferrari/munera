package com.application.munera.views.expenses;

import com.application.munera.data.Expense;
import com.application.munera.services.ExpenseService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Expenses")
@Route(value = "/", layout = MainLayout.class)
@PermitAll
public class ExpensesView extends VerticalLayout {

    private final ExpenseService expenseService;
    private final Grid<Expense> grid;

    @Autowired
    public ExpensesView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        this.grid = new Grid<>(Expense.class, false);

        addClassName("expenses-view");
        setSizeFull();
        configureGrid();
        add(grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("expense-grid");
        grid.setSizeFull();
        grid.addColumn(Expense::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Expense::getCost).setHeader("Amount").setSortable(true);
        grid.addColumn(Expense::getCategory).setHeader("Category").setSortable(true);
        grid.addColumn(Expense::getPeriodInterval).setHeader("Period Interval").setSortable(true);
        grid.addColumn(Expense::getPeriodUnit).setHeader("Period Unit").setSortable(true);
        grid.addColumn(Expense::getDate).setHeader("Date").setSortable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        List<Expense> expenses = expenseService.findAll();
        grid.setItems(expenses);
    }
}
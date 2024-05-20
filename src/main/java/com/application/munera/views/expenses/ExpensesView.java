package com.application.munera.views.expenses;

import com.application.munera.data.Category;
import com.application.munera.data.Expense;
import com.application.munera.repositories.ExpenseRepository;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Expenses")
@Route(value = "expenses", layout = MainLayout.class)
@PermitAll
public class ExpensesView extends VerticalLayout {

    private final ExpenseRepository expenseRepository;
    private final Grid<Expense> grid;

    @Autowired
    public ExpensesView(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
        this.grid = new Grid<>(Expense.class);
        addClassName("expenses-view");
        setSizeFull();

        configureGrid();
        add(grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("expense-grid");
        grid.setSizeFull();
        grid.setColumns("id", "name", "category.name", "cost", "description", "isPeriodic", "periodUnit", "periodInterval", "date");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        List<Expense> expenses = expenseRepository.findAll();
        grid.setItems(expenses);
    }
}
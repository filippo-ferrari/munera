package org.project.munera.controllers;

import org.project.munera.entities.Expense;
import org.project.munera.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExpensesController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/expenses")
    public List<Expense> list() {
        return expenseService.fetchExpenses();
    }
}

package org.project.munera.controllers;

import org.project.munera.entities.Expense;
import org.project.munera.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpensesController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/expenses")
    public List<Expense> list() {
        return expenseService.fetchExpenses();
    }

    @GetMapping("/expenses/{expense}")
    public Expense reference(@PathVariable Expense expense) {
        return expense;
    }

    @PostMapping("/expenses")
    public Expense create(@RequestBody Expense expense) {
        return this.expenseService.addNewExpense(expense);
    }

    @DeleteMapping("/expenses/{expense}")
    public void delete(@PathVariable Expense expense) {
        this.expenseService.deleteExpense(expense);
    }
}

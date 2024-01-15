package org.project.munera.controllers;

import org.project.munera.entities.Expense;
import org.project.munera.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpensesController {

    private final ExpenseService expenseService;

    public ExpensesController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

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

    @PatchMapping("/expenses/{expense}")
    public Expense patch(@PathVariable("expense") Expense expenseToPatch, @RequestBody Expense expensePatched) {
        return this.expenseService.patchExpense(expenseToPatch, expensePatched);
    }

    @DeleteMapping("/expenses/{expense}")
    public void delete(@PathVariable Expense expense) {
        this.expenseService.deleteExpense(expense);
    }
}

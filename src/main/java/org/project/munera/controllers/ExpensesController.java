package org.project.munera.controllers;

import org.project.munera.entities.Expense;
import org.project.munera.services.ExpenseService;
import org.project.munera.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
public class ExpensesController {

    private final ExpenseService expenseService;

    public ExpensesController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/expenses")
    public List<Expense> list(@RequestParam(required = false) String query,
                              @RequestParam(defaultValue = "0") Integer page,
                              @RequestParam(defaultValue = "500") Integer size,
                              @RequestParam(defaultValue = "") String sort) {
        final var filters = this.setUpLazyQuery(query, sort);
        return expenseService.fetchExpenses(filters);
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


    @ModelAttribute("filters")
    public Query setUpLazyQuery(@RequestParam(required = false) String query,
                                @RequestParam(required = false) String sort)
    {
        return Query.withSort(query, sort);
    }

    @ModelAttribute("page")
    public Pageable setUpPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "500") int size)
    {
        return PageRequest.of(page, size);
    }

}

package org.project.munera.controllers;

import org.project.munera.entities.PeriodicExpense;
import org.project.munera.services.PeriodicExpenseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class PeriodicExpenseController {

    private final PeriodicExpenseService expenseService;

    public PeriodicExpenseController(PeriodicExpenseService expenseService){
        this.expenseService = expenseService;
    }


    @GetMapping("/periodicExpenses")
    public List<PeriodicExpense> list() {
        return expenseService.fetchPeriodicExpenses();
    }

    @GetMapping("/periodicExpenses/{periodicExpense}")
    public PeriodicExpense reference(@PathVariable PeriodicExpense periodicExpense) {
        return periodicExpense;
    }

    @PostMapping("/periodicExpenses")
    public PeriodicExpense create(@RequestBody PeriodicExpense periodicExpense) {
        return this.expenseService.addNewPeriodicExpense(periodicExpense);
    }

    @PatchMapping("/periodicExpenses/{periodicExpense}")
    public PeriodicExpense patch(@PathVariable("periodExpense") PeriodicExpense expenseToPatch, @RequestBody PeriodicExpense expensePatched) {
        return this.expenseService.patchExpense(expenseToPatch, expensePatched);
    }

    @DeleteMapping("/periodicExpenses/{periodicExpense}")
    public void delete(@PathVariable PeriodicExpense periodicExpense) {
        this.expenseService.deletePeriodicExpense(periodicExpense);
    }
}

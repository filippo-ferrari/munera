package org.project.munera.controllers;

import org.project.munera.services.PeriodicExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeriodicExpenseController {

    private final PeriodicExpenseService expenseService;

    public PeriodicExpenseController(PeriodicExpenseService expenseService){
        this.expenseService = expenseService;
    }
}

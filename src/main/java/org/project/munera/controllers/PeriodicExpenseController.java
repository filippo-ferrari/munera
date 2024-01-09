package org.project.munera.controllers;

import org.project.munera.services.PeriodicExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeriodicExpenseController {

    @Autowired
    private PeriodicExpenseService expenseService;

}

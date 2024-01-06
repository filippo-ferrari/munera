package org.project.munera.services;

import org.project.munera.entities.Expense;
import org.project.munera.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> fetchExpenses() {
        return this.expenseRepository.findAll();
    }


}

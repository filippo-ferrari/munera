package org.project.munera.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.project.munera.entities.PeriodicExpense;
import org.project.munera.repositories.PeriodicExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodicExpenseService {

    @Autowired
    private PeriodicExpenseRepository periodicExpenseRepository;

    public List<PeriodicExpense> fetchPeriodicExpenses() {
        return this.periodicExpenseRepository.findAll();
    }

    public PeriodicExpense addNewPeriodicExpense(final @NonNull PeriodicExpense expense) {
        return this.periodicExpenseRepository.save(expense);
    }

    @Transactional
    public PeriodicExpense patchExpense(@NonNull PeriodicExpense expenseToPatch, PeriodicExpense patchedExpense) {
        return periodicExpenseRepository.findById(expenseToPatch.getId())
                .map(existingExpense -> {
                    existingExpense = patchedExpense;
                    return periodicExpenseRepository.save(existingExpense);
                })
                .orElseThrow(() -> new EntityNotFoundException(" PeriodicExpense with ID " + expenseToPatch.getId() + " not found."));    }

    public void deletePeriodicExpense(final @NonNull PeriodicExpense expense) {
        this.periodicExpenseRepository.delete(expense);
    }
}


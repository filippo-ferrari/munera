package org.project.munera.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.project.munera.entities.Expense;
import org.project.munera.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> fetchExpenses() {
        return this.expenseRepository.findAll();
    }

    public Expense addNewExpense(final @NonNull Expense expense) {
        this.setExpenseDate(expense);
        return this.expenseRepository.save(expense);
    }

    @Transactional
    public Expense patchExpense(@NonNull Expense expenseToPatch, Expense patchedExpense) {
        return expenseRepository.findById(expenseToPatch.getId())
                .map(existingExpense -> {
                    existingExpense = patchedExpense;
                    return expenseRepository.save(existingExpense);
                })
                .orElseThrow(() -> new EntityNotFoundException("Expense with ID " + expenseToPatch.getId() + " not found."));    }


    public void deleteExpense(final @NonNull Expense expense) {
        this.expenseRepository.delete(expense);
    }

    private void setExpenseDate(final @NonNull Expense expense) {
        if (Objects.isNull(expense.getDate())) expense.setDate(LocalDate.now());
    }
}

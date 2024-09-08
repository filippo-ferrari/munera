package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.ExpenseType;
import com.application.munera.data.Person;
import com.application.munera.repositories.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Retrieves an expense by its ID.
     * @param id the ID of the expense
     * @return an Optional containing the expense if found, otherwise empty
     */
    public Optional<Expense> get(Long id) {
        return expenseRepository.findById(id);
    }

    /**
     * Finds all expenses where the specified person is the beneficiary.
     * @param person the user of the expenses
     * @return the collection of expenses found
     */
    public Collection<Expense> findExpensesWhereBeneficiary(final Person person) {
        return expenseRepository.findExpensesByBeneficiary(person.getId());
    }

    /**
     * Finds all expenses where the specified person is the payer.
     * @param person the user of the expenses
     * @return the collection of expenses found
     */
    public Collection<Expense> findExpensesWherePayer(final Person person) {
        return expenseRepository.findExpensesByPayer(person.getId());
    }

    /**
     * Finds all expenses where the specified person is the beneficiary and the expense is unpaid.
     * @param person the user of the expenses
     * @return the collection of unpaid expenses found
     */
    public Collection<Expense> findUnpaidExpensesWhereBeneficiary(final Person person) {
        return expenseRepository.findUnapidExpensesByBeneficiary(person.getId());
    }

    /**
     * Finds all expenses where the specified person is the payer and the expense is unpaid.
     * @param person the user of the expenses
     * @return the collection of unpaid expenses found
     */
    public Collection<Expense> findUnpaidExpensesWherePayer(final Person person) {
        return expenseRepository.findUnpaidExpensesByPayer(person.getId());
    }

    /**
     * Finds all expenses related to a user, both where the user is a payer and a beneficiary.
     * @param person the user of the expenses
     * @return the list of expenses found
     */
    public List<Expense> findExpensesByUser(final Person person) {
        // Retrieve expenses where the person is the payer
        final var payerExpenses = this.findExpensesWherePayer(person);
        // Retrieve expenses where the person is the beneficiary
        final var beneficiaryExpenses = this.findExpensesWhereBeneficiary(person);
        // Combine both sets of expenses into a single list without duplicates
        return Stream.concat(payerExpenses.stream(), beneficiaryExpenses.stream())
                .distinct()
                .toList();
    }
    /**
     * Retrieves all expenses.
     * @return the list of all expenses
     */
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    /**
     * Finds all expenses for a given year.
     * @param year the year for which to find expenses
     * @return the list of expenses found
     */
    public List<Expense> findAllByYear(final int year) {
        return this.expenseRepository.findAllByYear(year);
    }

    /**
     * Fetches all expenses ordered by date in descending order.
     * @return the list of expenses found
     */
    public List<Expense> findAllOrderByDateDescending() {
        return this.expenseRepository.findAllByOrderByDateDesc();
    }

    /**
     * Finds expenses by year excluding those marked as credit and paid.
     * @param year the year for which to find expenses
     * @return the list of expenses found
     */
    public List<Expense> findExpensesByYearExcludingCreditPaid(int year) {
        return expenseRepository.findByYearAndFilterCreditPaid(year, ExpenseType.CREDIT);
    }

    /**
     * Checks if an expense has been paid.
     * @param expense the expense to check
     * @return true if the expense has been paid, false otherwise
     */
    public boolean isExpensePaid(final Expense expense) {
        return this.expenseRepository.existsByIdAndIsPaidTrue(expense.getId());
    }

    /**
     * Updates an existing expense.
     * @param entity the expense to update
     */
    public void update(Expense entity) {
        if (Boolean.TRUE.equals(entity.getIsPaid())) {
            entity.setPaymentDate(LocalDateTime.now());
        }
        this.setExpenseType(entity);
        expenseRepository.save(entity);
    }

    /**
     * Deletes an expense given its ID.
     * @param id the ID of the expense to delete
     */
    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }

    /**
     * Lists expenses in a paginated format.
     * @param pageable the pagination information
     * @return a page of expenses
     */
    public Page<Expense> list(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }

    /**
     * Lists expenses in a paginated format with filtering options.
     * @param pageable the pagination information
     * @param filter the filter specification
     * @return a page of expenses matching the filter
     */
    public Page<Expense> list(Pageable pageable, Specification<Expense> filter) {
        return expenseRepository.findAll(filter, pageable);
    }

    /**
     * Counts the total number of expenses.
     * @return the count of expenses
     */
    public int count() {
        return (int) expenseRepository.count();
    }

    // ================================
    // Private methods
    // ================================

    /**
     * Sets the expense type depending on the presence or absence of a payer and beneficiary.
     * This is used to filter expenses where the payer has been reimbursed.
     * @param expense the expense to set the type of
     */
    private void setExpenseType(final @Nonnull Expense expense) {
        // Check if the payer is present
        if (Objects.nonNull(expense.getPayer())) {
            // If payer is present, set type to CREDIT
            expense.setExpenseType(ExpenseType.CREDIT);
        }
        // Check if the beneficiary is present and no payer
        else if (Objects.nonNull(expense.getBeneficiary())) {
            // If beneficiary is present and no payer, set type to DEBIT
            expense.setExpenseType(ExpenseType.DEBIT);
        }
        // If neither payer nor beneficiary is present
        else {
            // Set type to NONE
            expense.setExpenseType(ExpenseType.NONE);
        }
    }
}
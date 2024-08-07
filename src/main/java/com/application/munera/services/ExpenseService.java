package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.repositories.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Optional<Expense> get(Long id) {
        return repository.findById(id);
    }

    /**
     * finds all expenses tagged as debit given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findDebtByUser(final Person person) {
        return repository.findDebtorsExpensesByPersonId(person.getId());
    }

    /**
     * finds all expenses tagged as credit given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findCreditByUser(final Person person) {
        return repository.findCreditorsExpensesByPersonId(person.getId());
    }

    /**
     * finds all expenses tagged as debit and unpaid given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findUnpaidDebtByUser(final Person person) {
        return repository.findUnpaidDebtorsExpensesByPersonId(person.getId());
    }

    /**
     * finds all expenses tagged as credit and unpaid given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findUnpaidCreditByUser(final Person person) {
        return repository.findUnpaidCreditorsExpensesByPersonId(person.getId());
    }

    /**
     * finds all expenses related to a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public List<Expense> findExpenseByUser(final Person person) {
        final var credits = this.findCreditByUser(person);
        final var debits = this.findDebtByUser(person);
        return Stream.concat(credits.stream(), debits.stream()).toList();
    }

    public List<Expense> findAll() {return repository.findAll();}

    /**
     * updates an expense
     * @param entity the expense to update
     */
    public void update(Expense entity) {
        if (Boolean.TRUE.equals(entity.getIsPaid())) entity.setPaymentDate(LocalDateTime.now());
        repository.save(entity);
    }

    /**
     * deletes an expense given the ID
     * @param id the id of the expense to delete
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Expense> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Expense> list(Pageable pageable, Specification<Expense> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<Expense> findAllByYear(final int year ) {
        return this.repository.findAllByYear(year);
    }

    /**
     * checks if an expense has been paid
     * @param expense the expense to check
     * @return true if the expense has been paid, false otherwise
     */
    public boolean isExpensePaid(final Expense expense) {
        return this.repository.existsByIdAndIsPaidTrue(expense.getId());
    }

    /**
     * fetches all expenses ordered by date descending
     * @return the list of expenses found
     */
    public List<Expense> findAllOrderByDateDescending() {
        return this.repository.findAllByOrderByDateDesc();
    }
}

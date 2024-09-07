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
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Optional<Expense> get(Long id) {
        return expenseRepository.findById(id);
    }

    /**
     * finds all expenses tagged as debit given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findDebtByUser(final Person person) {
        return expenseRepository.findDebtorsExpensesByPersonId(person.getId());
    }

    /**
     * finds all expenses tagged as credit given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findCreditByUser(final Person person) {
        return expenseRepository.findCreditorsExpensesByPersonId(person.getId());
    }

    /**
     * finds all expenses tagged as debit and unpaid given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findUnpaidDebtByUser(final Person person) {
        return expenseRepository.findUnpaidDebtorsExpensesByPersonId(person.getId());
    }

    /**
     * finds all expenses tagged as credit and unpaid given a user
     * @param person the user of the expenses
     * @return the collections of expenses found
     */
    public Collection<Expense> findUnpaidCreditByUser(final Person person) {
        return expenseRepository.findUnpaidCreditorsExpensesByPersonId(person.getId());
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

    public List<Expense> findAll() {return expenseRepository.findAll();}

    /**
     * updates an expense
     * @param entity the expense to update
     */
    public void update(Expense entity) {
        if (Boolean.TRUE.equals(entity.getIsPaid())) entity.setPaymentDate(LocalDateTime.now());
        this.setExpenseType(entity);
        expenseRepository.save(entity);
    }

    /**
     * deletes an expense given the ID
     * @param id the id of the expense to delete
     */
    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }

    public Page<Expense> list(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }

    public Page<Expense> list(Pageable pageable, Specification<Expense> filter) {
        return expenseRepository.findAll(filter, pageable);
    }

    public int count() {
        return (int) expenseRepository.count();
    }

    public List<Expense> findAllByYear(final int year ) {
        return this.expenseRepository.findAllByYear(year);
    }

    public List<Expense> findExpensesByYearExcludingCreditPaid(int year) {
        return expenseRepository.findByYearAndFilterCreditPaid(year, ExpenseType.CREDIT);
    }

    /**
     * checks if an expense has been paid
     * @param expense the expense to check
     * @return true if the expense has been paid, false otherwise
     */
    public boolean isExpensePaid(final Expense expense) {
        return this.expenseRepository.existsByIdAndIsPaidTrue(expense.getId());
    }

    /**
     * fetches all expenses ordered by date descending
     * @return the list of expenses found
     */
    public List<Expense> findAllOrderByDateDescending() {
        return this.expenseRepository.findAllByOrderByDateDesc();
    }

    /**
     *  sets the Expense type depending on the presence or absence of creditors and debtors
     *  this is used to filter expenses with a creditor that are paid, since they are not part of
     *  the actual money the user has spent, it's just a load technically
     * @param expense the expense to set the type of
     */
    private void setExpenseType(final @Nonnull Expense expense) {
        if (!expense.getCreditors().isEmpty())
            // If creditors are present, set type to CREDIT
            expense.setExpenseType(ExpenseType.CREDIT);
         else if (!expense.getDebtors().isEmpty())
            // If debtors are present and no creditors, set type to DEBIT
            expense.setExpenseType(ExpenseType.DEBIT);
         else
             // If neither creditors nor debtors are present, set type to NONE
            expense.setExpenseType(ExpenseType.NONE);
        }
}

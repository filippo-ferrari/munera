package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.ExpenseType;
import com.application.munera.data.Person;
import com.application.munera.repositories.ExpenseRepository;
import com.application.munera.repositories.PersonRepository;
import com.application.munera.repositories.UserRepository;
import com.application.munera.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository, PersonRepository personRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository =  userRepository;
        this.personRepository = personRepository;
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
        return expenseRepository.findUnpaidExpensesByBeneficiary(person.getId());
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
     * Finds all expenses related to a person, both where the person is a payer and a beneficiary.
     * @param person the person of the expenses
     * @return the list of expenses found
     */
    public List<Expense> findExpensesByPerson(final Person person) {
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
    public List<Expense> findAllOrderByDateDescending(Long userId) {
        return this.expenseRepository.findByUserIdOrderByDateDesc(userId);
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
    public void update(Expense entity, Long userId) {
        entity.setUserId(userId);
        if (Boolean.TRUE.equals(entity.getIsPaid())) entity.setPaymentDate(LocalDate.now());
        else entity.setPaymentDate(null);
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

    /**
     * Fetches the yearly net expenses of the user
     * @param loggedInPerson the logged-in user
     * @param year the year from which we want the expenses
     * @return the list of expenses of that user in that year
     */
    public List<Expense> fetchExpensesForDashboard(Person loggedInPerson, Year year) {
        List<Expense> totalExpenses = new ArrayList<>();
        final var yearValue = year.getValue();

        // Fetch expenses where you are the payer and beneficiary (self-expenses) for the given year
        List<Expense> bothExpenses = expenseRepository.findExpensesByPayerAndBeneficiaryAndYear(loggedInPerson.getId(), yearValue);
        totalExpenses.addAll(bothExpenses); // Include these regardless of isPaid status

        // Fetch expenses where you are the payer (you owe money), filtered by year
        List<Expense> beneficiaryExpenses = expenseRepository.findExpensesByBeneficiaryAndYear(loggedInPerson.getId(), yearValue);
        for (Expense expense : beneficiaryExpenses) {
            if (!totalExpenses.contains(expense)) totalExpenses.add(expense);
        }
        // Fetch expenses where you are the beneficiary and not paid (amount owed to you), filtered by year
        List<Expense> payerExpenses = expenseRepository.findExpensesByPayerAndYear(loggedInPerson.getId(), yearValue);
        for (Expense expense : payerExpenses) {
            if (Boolean.FALSE.equals(expense.getIsPaid()) && !totalExpenses.contains(expense)) totalExpenses.add(expense);
        }
        return totalExpenses;
    }

    // ================================
    // Private methods
    // ================================

    /**
     * Sets the expense type depending on the presence or absence of a payer and beneficiary.
     * @param expense the expense to set the type of
     */
    private void setExpenseType(final @Nonnull Expense expense) {
        // Get the currently logged-in user
        final var userDetails = SecurityUtils.getLoggedInUserDetails();
        if (userDetails == null) throw new IllegalStateException("No logged-in user found");
        // Fetch the logged-in user
        final var loggedInUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Person loggedInPerson = this.personRepository.findByUsername(loggedInUser.getUsername());

        if (loggedInPerson == null) throw new IllegalStateException("No associated Person entity found for logged-in user");

        // Check if the payer and beneficiary are present
        Person payer = expense.getPayer();
        Person beneficiary = expense.getBeneficiary();

        // Determine the expense type
        if (payer.equals(loggedInPerson) && !beneficiary.equals(loggedInPerson)) {
            // Logged-in user is the payer, and the beneficiary is someone else
            expense.setExpenseType(ExpenseType.CREDIT);
        } else if (!payer.equals(loggedInPerson) && beneficiary.equals(loggedInPerson)) {
            // Logged-in user is the beneficiary, and the payer is someone else
            expense.setExpenseType(ExpenseType.DEBIT);
        } else if (payer.equals(loggedInPerson) &&  beneficiary.equals(loggedInPerson)) {
            // Both payer and beneficiary are the logged-in user
            expense.setExpenseType(ExpenseType.NONE);
        }
    }
}
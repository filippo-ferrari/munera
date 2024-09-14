package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.data.User;
import com.application.munera.repositories.ExpenseRepository;
import com.application.munera.repositories.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final ExpenseRepository expenseRepository;

    public PersonService(PersonRepository personRepository, ExpenseRepository expenseRepository) {
        this.personRepository = personRepository;
        this.expenseRepository = expenseRepository;
    }

    /**
     * Finds a person by ID.
     * @param id the ID of the person
     * @return an optional containing the person if found, otherwise empty
     */
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    /**
     * Finds all persons.
     * @return a collection of all persons
     */
    public List<Person> findAllByUserId(Long userId) {
        return this.personRepository.findByUserId(userId);
    }

    public Person findByUsername(String username) {
        return this.personRepository.findByUsername(username);
    }

    /**
     * Finds all people excluding the users'ones.
     * @return a collection of all persons
     */
    public List<Person> findAllExcludeLoggedUser(User user) {
        final var userId = user.getId();
        final var username = user.getUsername();
        return this.personRepository.findAllByUserIdExcludingPerson(userId, username);
    }

    /**
     * Lists all persons with pagination.
     * @param pageable the pagination information
     * @return a page of persons
     */
    public Page<Person> list(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    /**
     * Lists all persons with pagination and filtering.
     * @param pageable the pagination information
     * @param filter the specification filter
     * @return a page of persons matching the filter
     */
    public Page<Person> list(Pageable pageable, Specification<Person> filter) {
        return this.personRepository.findAll(filter, pageable);
    }

    /**
     * Counts the total number of persons.
     * @return the total count of persons
     */
    public int count() {
        return (int) this.personRepository.count();
    }


    /**
     * Updates a person in the repository.
     * @param person the person to update
     */
    public void update(Person person, Long userId) {
        person.setUserId(userId);
        this.personRepository.save(person);
    }

    /**
     * Deletes a person by ID.
     * @param id the ID of the person to delete
     */
    public void delete(Long id) {
        this.personRepository.deleteById(id);
    }

    /**
     * Calculates the total debt of a person.
     * @param person the person whose debt is to be calculated
     * @return the total debt amount
     */
    public BigDecimal calculateDebt(final Person person) {
        return this.expenseRepository.findExpensesByPayer(person.getId()).stream()
                .filter(expense -> !expense.getBeneficiary().equals(person) && Boolean.FALSE.equals(expense.getIsPaid()))
                .map(Expense::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the total credit of a person.
     * @param person the person whose credit is to be calculated
     * @return the total credit amount
     */
    public BigDecimal calculateCredit(final Person person) {
        return this.expenseRepository.findExpensesByBeneficiary(person.getId()).stream()
                .filter(expense -> !expense.getPayer().equals(person) && Boolean.FALSE.equals(expense.getIsPaid()))
                .map(Expense::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the net balance of a person.
     * The net balance is the difference between the total amount the person is owed
     * (expenses where they are the payer) and the total amount the person owes
     * (expenses where they are the beneficiary).
     *
     * A positive net balance means the person is owed money.
     * A negative net balance means the person owes money.
     *
     * @param person the person whose net balance is to be calculated
     * @return the net balance amount
     */
    public BigDecimal calculateNetBalance(final Person person) {
            // Calculate total debt (what others owe to the person)
            final BigDecimal debt = this.calculateDebt(person);
            // Calculate total credit (what the person owes to others)
            final BigDecimal credit = this.calculateCredit(person);
            // Net balance calculation: debt (owed to the person) - credit (person owes)
            return debt.subtract(credit);
    }
}
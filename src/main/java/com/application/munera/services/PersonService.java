package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.repositories.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final ExpenseService expenseService;

    public PersonService(PersonRepository personRepository, ExpenseService expenseService) {
        this.personRepository = personRepository;
        this.expenseService = expenseService;
    }

    public Optional<Person> get(Long id) {
        return personRepository.findById(id);
    }

    public Collection<Person> findAll() {
        return this.personRepository.findAll();
    }

    public void update(Person person) {
        this.personRepository.save(person);
    }

    public void delete(Long id) {
        this.personRepository.deleteById(id);
    }

    public Page<Person> list(Pageable pageable){
        return personRepository.findAll(pageable);
    }

    public Page<Person> list(Pageable pageable, Specification<Person> filter) {
        return  this.personRepository.findAll(filter, pageable);
    }

    public int count() {
        return (int) this.personRepository.count();
    }

    public BigDecimal calculateDebt(final Person person){
        return this.expenseService.findDebtByUser(person).stream().map(Expense::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateCredit(final Person person) {
        return this.expenseService.findCreditByUser(person).stream().map(Expense::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateNetBalance(final Person person) {
        final var credit = this.expenseService.findCreditByUser(person).stream().map(Expense::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        final var debit = this.expenseService.findDebtByUser(person).stream().map(Expense::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return credit.subtract(debit);
    }
}

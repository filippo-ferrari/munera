package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.repositories.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Optional<Expense> get(Long id) {
        return repository.findById(id);
    }

    public Collection<Expense> findDebtByUser(final Person person) {
        return repository.findDebtorsExpensesByPersonId(person.getId());
    }

    public Collection<Expense> findCreditByUser(final Person person) {
        return repository.findCreditorsExpensesByPersonId(person.getId());
    }

    public Collection<Expense> findUnpaidDebtByUser(final Person person) {
        return repository.findUnpaidDebtorsExpensesByPersonId(person.getId());
    }

    public Collection<Expense> findUnpaidCreditByUser(final Person person) {
        return repository.findUnpaidCreditorsExpensesByPersonId(person.getId());
    }
    public List<Expense> findAll() {return repository.findAll();}

    public void update(Expense entity) {
        repository.save(entity);
    }

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

    public boolean isExpenseResolved(final Expense expense) {
        return this.repository.existsByIdAndIsResolvedTrue(expense.getId());
    }

    public List<Expense> findAllOrderByDateDescending() {
        return this.repository.findAllByOrderByDateDesc();
    }

}

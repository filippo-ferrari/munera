package com.application.munera.repositories;


import com.application.munera.data.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpenseRepository
        extends
            JpaRepository<Expense, Long>,
            JpaSpecificationExecutor<Expense> {

}

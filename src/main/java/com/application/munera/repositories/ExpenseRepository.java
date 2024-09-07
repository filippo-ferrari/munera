package com.application.munera.repositories;


import com.application.munera.data.Expense;
import com.application.munera.data.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    // Find expenses where the creditor is a specific person
    @Query("SELECT e FROM Expense e WHERE e.creditor.id = :personId")
    Set<Expense> findCreditorsExpensesByPersonId(@Param("personId") Long personId);

    // Find expenses where the debtor is a specific person
    @Query("SELECT e FROM Expense e WHERE e.debtor.id = :personId")
    Set<Expense> findDebtorsExpensesByPersonId(@Param("personId") Long personId);

    // Find all expenses for a given year
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year")
    List<Expense> findAllByYear(@Param("year") int year);

    // Find unpaid expenses where the creditor is a specific person
    @Query("SELECT e FROM Expense e WHERE e.creditor.id = :personId AND e.isPaid = false")
    Set<Expense> findUnpaidCreditorsExpensesByPersonId(@Param("personId") Long personId);

    // Find unpaid expenses where the debtor is a specific person
    @Query("SELECT e FROM Expense e WHERE e.debtor.id = :personId AND e.isPaid = false")
    Set<Expense> findUnpaidDebtorsExpensesByPersonId(@Param("personId") Long personId);

    // Find expenses for a given year and filter by expense type and paid status
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND NOT (e.expenseType = :expenseType AND e.isPaid = true)")
    List<Expense> findByYearAndFilterCreditPaid(@Param("year") int year, @Param("expenseType") ExpenseType expenseType);

    // Check if an expense with the given ID exists and is paid
    boolean existsByIdAndIsPaidTrue(Long id);

    // Find all expenses ordered by date descending
    List<Expense> findAllByOrderByDateDesc();
}
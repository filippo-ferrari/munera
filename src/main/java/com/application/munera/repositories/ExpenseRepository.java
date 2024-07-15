package com.application.munera.repositories;


import com.application.munera.data.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    @Query("SELECT e FROM Expense e JOIN e.creditors c WHERE c.id = :personId")
    Set<Expense> findCreditorsExpensesByPersonId(@Param("personId") Long personId);

    @Query("SELECT e FROM Expense e JOIN e.debtors d WHERE d.id = :personId")
    Set<Expense> findDebtorsExpensesByPersonId(@Param("personId") Long personId);

    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year")
    List<Expense> findAllByYear(@Param("year") int year);

    @Query("SELECT e FROM Expense e JOIN e.creditors c WHERE c.id = :personId AND e.isResolved = false")
    Set<Expense> findUnpaidCreditorsExpensesByPersonId(@Param("personId") Long personId);

    @Query("SELECT e FROM Expense e JOIN e.debtors d WHERE d.id = :personId AND e.isResolved = false")
    Set<Expense> findUnpaidDebtorsExpensesByPersonId(@Param("personId") Long personId);

    boolean existsByIdAndIsResolvedTrue(Long id);

    List<Expense> findAllByOrderByDateDesc();}
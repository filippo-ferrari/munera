package com.application.munera.repositories;


import com.application.munera.data.Expense;
import com.application.munera.data.enums.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    @Query("SELECT DISTINCT YEAR(e.date) FROM Expense e WHERE e.userId = :userId ORDER BY YEAR(e.date)")
    List<Integer> findExpenseYearsByUserId(@Param("userId") Long userId);

    // Find expenses where the payer is a specific person
    @Query("SELECT e FROM Expense e WHERE e.payer.id = :personId")
    List<Expense> findExpensesByPayer(@Param("personId") Long personId);

    // Find expenses where the beneficiary is a specific person
    @Query("SELECT e FROM Expense e WHERE e.beneficiary.id = :personId")
    List<Expense> findExpensesByBeneficiary(@Param("personId") Long personId);

    // Find expenses where both payer and beneficiary are the same person
    @Query("SELECT e FROM Expense e WHERE e.payer.id = :personId AND e.beneficiary.id = :personId")
    List<Expense> findExpensesByPayerAndBeneficiary(@Param("personId") Long personId);

    // Find expenses where the payer and beneficiary are the same person for a specific year
    @Query("SELECT e FROM Expense e WHERE e.payer.id = :personId AND e.beneficiary.id = :personId AND YEAR(e.date) = :year")
    List<Expense> findExpensesByPayerAndBeneficiaryAndYear(@Param("personId") Long personId, @Param("year") int year);

    // Find expenses where the payer is a specific person for a specific year
    @Query("SELECT e FROM Expense e WHERE e.payer.id = :personId AND YEAR(e.date) = :year")
    List<Expense> findExpensesByPayerAndYear(@Param("personId") Long personId, @Param("year") int year);

    // Find expenses where the beneficiary is a specific person for a specific year
    @Query("SELECT e FROM Expense e WHERE e.beneficiary.id = :personId AND YEAR(e.date) = :year")
    List<Expense> findExpensesByBeneficiaryAndYear(@Param("personId") Long personId, @Param("year") int year);
    // Find all expenses for a given year
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year")
    List<Expense> findAllByYear(@Param("year") int year);

    // Find unpaid expenses where the creditor is a specific person
    @Query("SELECT e FROM Expense e WHERE e.payer.id = :personId AND e.isPaid = false")
    Set<Expense> findUnpaidExpensesByPayer(@Param("personId") Long personId);

    // Find unpaid expenses where the debtor is a specific person
    @Query("SELECT e FROM Expense e WHERE e.beneficiary.id = :personId AND e.isPaid = false")
    Set<Expense> findUnpaidExpensesByBeneficiary(@Param("personId") Long personId);

    // Find expenses for a given year and filter by expense type and paid status
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND NOT (e.expenseType = :expenseType AND e.isPaid = true)")
    List<Expense> findByYearAndFilterCreditPaid(@Param("year") int year, @Param("expenseType") ExpenseType expenseType);

    // Check if an expense with the given ID exists and is paid
    boolean existsByIdAndIsPaidTrue(Long id);

    // Find all expenses ordered by date descending
    List<Expense> findByUserIdOrderByDateDesc(Long userId);
}
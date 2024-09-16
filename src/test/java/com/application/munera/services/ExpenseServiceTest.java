package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private Person loggedInPerson;

    @InjectMocks
    private ExpenseService expenseService;

    private Year year;

    @BeforeEach
    public void setUp() {
        year = Year.of(2023);
        when(loggedInPerson.getId()).thenReturn(1L);
    }

    @Test
    void testFetchExpensesForDashboard_NoExpenses() {
        when(expenseRepository.findExpensesByPayerAndBeneficiaryAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(new ArrayList<>());
        when(expenseRepository.findExpensesByBeneficiaryAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(new ArrayList<>());
        when(expenseRepository.findExpensesByPayerAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(new ArrayList<>());

        List<Expense> result = expenseService.fetchExpensesForDashboard(loggedInPerson, year);

        assertEquals(0, result.size(), "Expected no expenses to be fetched");
    }

    @Test
    @Disabled("will need to become integration test")
    void testFetchExpensesForDashboard_WithSelfExpenses() {
        Expense selfExpense = new Expense(); // Create a dummy Expense object
        List<Expense> bothExpenses = List.of(selfExpense);
        when(expenseRepository.findExpensesByPayerAndBeneficiaryAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(bothExpenses);
        when(expenseRepository.findExpensesByBeneficiaryAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(new ArrayList<>());
        when(expenseRepository.findExpensesByPayerAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(new ArrayList<>());

        List<Expense> result = expenseService.fetchExpensesForDashboard(loggedInPerson, year);

        assertEquals(1, result.size(), "Expected one self-expense to be fetched");
        assertEquals(selfExpense, result.get(0), "Expected the self-expense to match");
    }

    @Test
    @Disabled("will need to become integration test")
    void testFetchExpensesForDashboard_WithUnpaidExpenses() {
        Expense unpaidExpense = new Expense();
        unpaidExpense.setIsPaid(false);
        when(expenseRepository.findExpensesByPayerAndBeneficiaryAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(new ArrayList<>());
        when(expenseRepository.findExpensesByBeneficiaryAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(new ArrayList<>());
        when(expenseRepository.findExpensesByPayerAndYear(loggedInPerson.getId(), year.getValue()))
                .thenReturn(List.of(unpaidExpense));

        List<Expense> result = expenseService.fetchExpensesForDashboard(loggedInPerson, year);

        assertEquals(1, result.size(), "Expected one unpaid expense to be fetched");
        assertEquals(unpaidExpense, result.getFirst(), "Expected the unpaid expense to match");
    }
}
package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersonServiceTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private PersonService personService;

    @Test
    public void calculateDeb_whenExpensesAreMixed() {
        Person person = new Person();
        person.setLastName("first");
        person.setFirstName("person");

        Person person2 = new Person();
        person2.setLastName("second");
        person2.setFirstName("person");

//        Person person3 = new Person();
//        person3.setLastName("third");
//        person3.setFirstName("person");

        Expense expense1 = mock(Expense.class);
        when(expense1.getPayer()).thenReturn(person);
        when(expense1.getBeneficiary()).thenReturn(person2);
        when(expense1.getCost()).thenReturn(new BigDecimal("110.00"));
        when(expense1.getIsPaid()).thenReturn(false);

        Expense expense2 = mock(Expense.class);
        when(expense2.getPayer()).thenReturn(person);
        when(expense2.getBeneficiary()).thenReturn(person);
        when(expense2.getCost()).thenReturn(new BigDecimal("50.00"));
        when(expense2.getIsPaid()).thenReturn(false);

        Expense expense3 = mock(Expense.class);
        when(expense3.getPayer()).thenReturn(person);
        when(expense3.getBeneficiary()).thenReturn(person);
        when(expense3.getCost()).thenReturn(new BigDecimal("50.00"));
        when(expense3.getIsPaid()).thenReturn(true);

        when(expenseService.findExpensesWherePayer(person)).thenReturn(List.of(expense1, expense2, expense3));

        BigDecimal totalDebt = personService.calculateDebt(person);

        assertEquals(new BigDecimal("110.00"), totalDebt);
    }
    @Test
    void calculateDebt_NoExpenses() {
        // Arrange
        Person person = new Person();
        when(expenseService.findExpensesWherePayer(person)).thenReturn(Collections.emptyList());

        // Act
        BigDecimal totalDebt = personService.calculateDebt(person);

        // Assert
        assertEquals(BigDecimal.ZERO, totalDebt);
    }

    @Test
    void calculateDebt_AllExpensesPaid() {
        // Arrange
        Person person = new Person();

        Expense expense1 = mock(Expense.class);
        when(expense1.getPayer()).thenReturn(person);
        when(expense1.getBeneficiary()).thenReturn(new Person());
        when(expense1.getCost()).thenReturn(new BigDecimal("100.00"));
        when(expense1.getIsPaid()).thenReturn(true);

        Expense expense2 = mock(Expense.class);
        when(expense2.getPayer()).thenReturn(person);
        when(expense2.getBeneficiary()).thenReturn(new Person());
        when(expense2.getCost()).thenReturn(new BigDecimal("50.00"));
        when(expense2.getIsPaid()).thenReturn(true);

        when(expenseService.findExpensesWherePayer(person)).thenReturn(List.of(expense1, expense2));

        // Act
        BigDecimal totalDebt = personService.calculateDebt(person);

        // Assert
        assertEquals(BigDecimal.ZERO, totalDebt);
    }

    @Test
    void calculateDebt_ExpensesWithSamePayerAndBeneficiary() {
        // Arrange
        Person person = new Person();

        Expense expense1 = mock(Expense.class);
        when(expense1.getPayer()).thenReturn(person);
        when(expense1.getBeneficiary()).thenReturn(person); // Same person as payer
        when(expense1.getCost()).thenReturn(new BigDecimal("100.00"));
        when(expense1.getIsPaid()).thenReturn(false);

        when(expenseService.findExpensesWherePayer(person)).thenReturn(List.of(expense1));

        // Act
        BigDecimal totalDebt = personService.calculateDebt(person);

        // Assert
        assertEquals(BigDecimal.ZERO, totalDebt);
    }

    //TODO: technically calculate will only be invoked once an expense has been created, still
    //TODO: needs might need some fixing to take into account an exmpty list!!!
    @Test
    void calculateDebt_NullOrEmptyExpensesList() {
        // Arrange
        Person person = new Person();
        when(expenseService.findExpensesWherePayer(person)).thenReturn(null); // Null case

        // Act
        BigDecimal totalDebt = personService.calculateDebt(person);

        // Assert
        assertEquals(BigDecimal.ZERO, totalDebt);
    }

    @Test
    void calculateDebt_ExpensesWithNullAttributes() {
        // Arrange
        Person person = new Person();

        Expense expense1 = mock(Expense.class);
        when(expense1.getPayer()).thenReturn(person);
        when(expense1.getBeneficiary()).thenReturn(new Person());
        when(expense1.getCost()).thenReturn(null); // Null cost
        when(expense1.getIsPaid()).thenReturn(false);

        when(expenseService.findExpensesWherePayer(person)).thenReturn(List.of(expense1));

        // Act
        BigDecimal totalDebt = personService.calculateDebt(person);

        // Assert
        assertEquals(BigDecimal.ZERO, totalDebt);
    }

    //TODO: fix the bug that allows expenses with negative cost!!!
    @Test
    void calculateDebt_ExpenseWithNegativeCost() {
        // Arrange
        Person person = new Person();

        Expense expense1 = mock(Expense.class);
        when(expense1.getPayer()).thenReturn(person);
        when(expense1.getBeneficiary()).thenReturn(new Person());
        when(expense1.getCost()).thenReturn(new BigDecimal("-50.00")); // Negative cost
        when(expense1.getIsPaid()).thenReturn(false);

        when(expenseService.findExpensesWherePayer(person)).thenReturn(List.of(expense1));

        // Act
        BigDecimal totalDebt = personService.calculateDebt(person);

        // Assert
        assertEquals(new BigDecimal("-50.00"), totalDebt);
    }

}
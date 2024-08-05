package com.application.munera.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "expenses")
public class Expense extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "Name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "CategoryId", nullable = false)
    private Category category;

    @Column(name = "Cost", nullable = false)
    private BigDecimal cost;

    @Column(name = "Description")
    private String description;

    @Column(name = "PeriodicExpense", nullable = false)
    private Boolean isPeriodic;

    @Column(name = "PeriodUnit")
    private PeriodUnit periodUnit;

    @Column(name = "PeriodInterval")
    private Integer periodInterval;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Creditor_expenses",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "people_id"))
    private Set<Person> creditors;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Debtors_expenses",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "people_id"))
    private Set<Person> debtors;

    @ManyToOne
    @JoinColumn(name = "EventId")
    private Event event;

    @Column(name = "Date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate date;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    /**
     * the isResolved field starts as always false
     */
    @Column(name = "isResolved", nullable = false)
    private Boolean isResolved = false;
}

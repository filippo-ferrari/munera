package com.application.munera.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "expenses")
public class Expense {

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

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "PeriodUnit")
    private PeriodUnit periodUnit;

    @Column(name = "PeriodInterval")
    private Integer periodInterval;

    @ManyToOne
    @JoinColumn(name = "CreditorId")
    private Person payer;

    @ManyToOne
    @JoinColumn(name = "DebtorId")
    private Person beneficiary;

    @ManyToOne
    @JoinColumn(name = "EventId")
    private Event event;

    @Column(name = "Date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate date;

    @Column(name = "PaymentDate")
    private LocalDate paymentDate;

    @Column(name = "isPaid", nullable = false)
    private Boolean isPaid = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "expenseType", nullable = false)
    private ExpenseType expenseType;
}
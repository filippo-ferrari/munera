package com.application.munera.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Expense  {

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
    }

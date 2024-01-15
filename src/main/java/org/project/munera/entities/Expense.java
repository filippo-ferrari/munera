package org.project.munera.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "Creditor_expenses",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<Person> creditors;

    @Column(name = "Date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate date;
}

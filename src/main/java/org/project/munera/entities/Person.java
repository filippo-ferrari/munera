package org.project.munera.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "people")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Size(max = 100)
    @Column(name = "LastName", nullable = false)
    private String lastName;

    @JsonIgnore
    @ManyToMany(mappedBy = "creditors")
    private Set<Expense> creditorExpenses;

    @JsonIgnore
    @ManyToMany(mappedBy = "debtors")
    private Set<Expense> debtorExpenses;
}

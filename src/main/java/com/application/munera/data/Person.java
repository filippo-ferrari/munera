package com.application.munera.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "people")
public class Person extends AbstractEntity {

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

    @Email
    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @Column(name = "debt")
    private BigDecimal debt;

    @Column(name = "credit")
    private BigDecimal credit;

    @ManyToMany(mappedBy = "creditors")
    private Set<Expense> creditorExpenses;

    @ManyToMany(mappedBy = "debtors")
    private Set<Expense> debtorExpenses;

    @ManyToMany(mappedBy = "participants")
    private Set<Event> events;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Person other)) {
            return false;
        }
        return Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName) &&
                Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email);
    }
}
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

    @Email
    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @Column(name = "debt")
    private BigDecimal debt;

    @Column(name = "credit")
    private BigDecimal credit;

    @OneToMany(mappedBy = "payer")
    private Set<Expense> expensesAsPayer;

    @OneToMany(mappedBy = "beneficiary")
    private Set<Expense> expensesAsBeneficiary;

    @Column(name = "Username", unique = true)
    private String username; // This field will link to the User entity

    @Column(name = "UserId", unique = true)
    private Long userId; // Reference to the User entity

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
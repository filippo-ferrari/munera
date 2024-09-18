package com.application.munera.data;

import com.application.munera.data.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "users")
public class User {

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

    @Size(max = 100)
    @Column(name = "username",unique = true, nullable = false)
    private String username;

    @Size(max = 100)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "roles", nullable = false)
    private String roles;

    @Email
    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @Column(name = "monthlyIncome")
    private Double monthlyIncome;

    // Helper methods to handle roles as a list of enum values
    public List<Role> getRoleList() {
        if (roles == null || roles.isEmpty()) return new ArrayList<>();  // Return an empty list if roles are null or empty
        return Arrays.stream(roles.split(","))
                .map(Role::valueOf)
                .toList();
    }

    public void setRoleList(List<Role> roleList) {
        this.roles = roleList.stream()
                .map(Role::name)
                .collect(Collectors.joining(","));
    }
}

package com.application.munera.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String roles;
    private String email;
}
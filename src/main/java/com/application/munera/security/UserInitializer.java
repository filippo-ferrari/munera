package com.application.munera.security;

import com.application.munera.data.User;
import com.application.munera.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminProperties adminProperties;

    @PostConstruct
    public void init() {
        if (userService.count() == 0) {
            User adminUser = new User();
            adminUser.setUsername(adminProperties.getUsername());
            adminUser.setPassword(adminProperties.getPassword());
            adminUser.setFirstName(adminProperties.getFirstName());
            adminUser.setLastName(adminProperties.getLastName());
            adminUser.setRoles(adminProperties.getRoles());
            adminUser.setEmail(adminProperties.getEmail());
            userService.saveUserAndConnectedPerson(adminUser);
        }
    }
}

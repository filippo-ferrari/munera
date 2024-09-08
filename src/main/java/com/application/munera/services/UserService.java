package com.application.munera.services;

import com.application.munera.data.User;
import com.application.munera.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername (String username) {
        return this.userRepository.findByUsername(username);
    }
}

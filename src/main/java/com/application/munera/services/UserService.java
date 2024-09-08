package com.application.munera.services;

import com.application.munera.data.User;
import com.application.munera.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static com.application.munera.SecurityUtils.getLoggedInUserDetails;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername (String username) {
        return this.userRepository.findByUsername(username);
    }

    /**
     * Fetches the logged-in User entity.
     * @return User entity of the logged-in user, or null if not found.
     */
    public User getLoggedInUser() {
        UserDetails userDetails = getLoggedInUserDetails();
        if (userDetails != null) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username);
        }
        return null;
    }

}

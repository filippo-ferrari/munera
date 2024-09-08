package com.application.munera.services;

import com.application.munera.data.Person;
import com.application.munera.data.User;
import com.application.munera.repositories.PersonRepository;
import com.application.munera.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static com.application.munera.SecurityUtils.getLoggedInUserDetails;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public UserService(UserRepository userRepository, PersonRepository personRepository) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
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

    public void updateUser(User user) {
        userRepository.save(user);
        Person person = personRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Associated Person not found"));
        person.setFirstName(user.getFirstName());
        person.setLastName(user.getLastName());
        personRepository.save(person);
    }}

package com.application.munera.services;

import com.application.munera.data.Person;
import com.application.munera.data.User;
import com.application.munera.repositories.PersonRepository;
import com.application.munera.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.application.munera.security.SecurityUtils.getLoggedInUserDetails;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public UserService(UserRepository userRepository, PersonRepository personRepository) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public Optional<User> findById(final Long id) {
        return this.userRepository.findById(id);
    }

    public Optional<User> findByUsername (String username) {
        return this.userRepository.findByUsername(username);
    }

    /**
     * Fetches the logged-in User entity.
     *
     * @return User entity of the logged-in user, or null if not found.
     */
    public User getLoggedInUser() {
        final var userDetails = getLoggedInUserDetails();
        if (userDetails == null) throw new IllegalStateException("User is not logged in.");
        final var username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
    }

    @Transactional
    public void saveOrUpdateUserAndConnectedPerson(User user) {
        // Check if the user already exists in the database
        final var existingUserOptional = userRepository.findByUsername(user.getUsername());
        User userToSave = getUser(user, existingUserOptional);

        // Save the user entity
        userRepository.save(userToSave);

        // Check if the associated person exists for the user
        final var existingPersonOptional = personRepository.findOptionalByUsername(userToSave.getUsername());

        if (existingPersonOptional.isPresent()) {
            // If person exists, update the person entity
            Person personToUpdate = existingPersonOptional.get();
            personToUpdate.setFirstName(userToSave.getFirstName());
            personToUpdate.setLastName(userToSave.getLastName());
            personToUpdate.setUsername(user.getUsername());
            personToUpdate.setEmail(userToSave.getEmail());
            personRepository.save(personToUpdate);
        } else {
            // If no person is associated with the user, create a new Person entity and link it to the User
            Person newPerson = new Person();
            newPerson.setFirstName(userToSave.getFirstName());
            newPerson.setLastName(userToSave.getLastName());
            newPerson.setEmail(userToSave.getEmail());
            newPerson.setUsername(userToSave.getUsername());
            newPerson.setUserId(userToSave.getId());
            personRepository.save(newPerson);
        }
    }

    private User getUser(User user, Optional<User> existingUserOptional) {
        User userToSave;

        if (existingUserOptional.isPresent()) {
            // If user exists, update the user entity
            userToSave = existingUserOptional.get();
            userToSave.setFirstName(user.getFirstName());
            userToSave.setLastName(user.getLastName());
            userToSave.setEmail(user.getEmail());
            userToSave.setUsername(user.getUsername());
            userToSave.setPassword(user.getPassword());
            userToSave.setRoles(user.getRoles());
        } else {
            // If user does not exist, save the new user entity
            userToSave = user;
        }
        return userToSave;
    }

    public Long count() {
        return  this.userRepository.count();
    }

    public void delete(final User user) {
        this.userRepository.delete(user);
        final var person = this.personRepository.findByUsername(user.getUsername());
        this.personRepository.delete(person);
    }
}

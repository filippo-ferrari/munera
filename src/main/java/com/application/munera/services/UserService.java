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

    /**
     * Retrieves all {@link User} entities.
     *
     * @return a list of all users
     */
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    /**
     * Finds a {@link User} entity by its ID.
     *
     * @param id the ID of the user to find
     * @return an {@link Optional} containing the user if found, or an empty {@link Optional} if not found
     */
    public Optional<User> findById(final Long id) {
        return this.userRepository.findById(id);
    }

    /**
     * Finds a {@link User} entity by its username.
     *
     * @param username the username of the user to find
     * @return an {@link Optional} containing the user if found, or an empty {@link Optional} if not found
     */
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    /**
     * Fetches the {@link User} entity of the currently logged-in user.
     *
     * @return the logged-in {@link User} entity
     * @throws IllegalStateException if the user is not logged in or not found
     */
    public User getLoggedInUser() {
        final var userDetails = getLoggedInUserDetails();
        if (userDetails == null) throw new IllegalStateException("User is not logged in.");
        final var username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
    }

    /**
     * Saves or updates a {@link User} entity and its associated {@link Person} entity.
     * If the user already exists, it updates the user and the associated person.
     * If the user does not exist, it creates a new user and a new person entity.
     *
     * @param user the {@link User} entity to save or update
     */
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

    /**
     * Counts the total number of {@link User} entities.
     *
     * @return the total number of users
     */
    public Long count() {
        return this.userRepository.count();
    }

    /**
     * Deletes the specified {@link User} entity and its associated {@link Person} entity.
     *
     * @param user the {@link User} entity to delete
     */
    public void delete(final User user) {
        this.userRepository.delete(user);
        final var person = this.personRepository.findByUsername(user.getUsername());
        this.personRepository.delete(person);
    }

    /**
     * Determines whether a user exists and creates or updates the {@link User} entity accordingly.
     *
     * @param user the {@link User} entity to save or update
     * @param existingUserOptional an {@link Optional} containing an existing user if found
     * @return the user to be saved
     */
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
            userToSave.setMonthlyIncome(user.getMonthlyIncome());
        } else {
            userToSave = user; // If user does not exist, save the new user entity
        }
        return userToSave;
    }
}
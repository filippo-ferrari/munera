package com.application.munera.services;

import com.application.munera.data.Person;
import com.application.munera.data.User;
import com.application.munera.repositories.PersonRepository;
import com.application.munera.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    public Optional<User> findByUsername (String username) {
        return this.userRepository.findByUsername(username);
    }

    /**
     * Fetches the logged-in User entity.
     *
     * @return User entity of the logged-in user, or null if not found.
     */
    public Optional<User> getLoggedInUser() {
        UserDetails userDetails = getLoggedInUserDetails();
        if (userDetails != null) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username);
        }
        return null;
    }

    /**
     * Updates the user's data and its connected person entity
     * @param user the user of which we update the data
     */
    public void updateUser(User user) {
        userRepository.save(user);
        final var person = personRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Associated Person not found"));
        person.setFirstName(user.getFirstName());
        person.setLastName(user.getLastName());
        personRepository.save(person);
    }

    /**
     * Saves a user and connected person entity
     * @param user the user of which we update the data
     */
    public void saveUserAndConnectedPerson(User user) {
        //TODO: look if this method can substitute the one above: updateUser, they seem to do similar things

        // Check if the user already exists in the database
        final var existingUserOptional = userRepository.findByUsername(user.getUsername());

        User existingUser;

        // Save the new user entity if he doesn't exist yet
        existingUser = existingUserOptional.orElseGet(() -> userRepository.save(user));

        // Check if the associated person exists for the user
        final var existingPerson = personRepository.findByUserId(existingUser.getId());

        if (existingPerson.isEmpty()) {
            // If no person is associated with the user, create a new Person entity and link it to the User
            Person person = new Person();
            person.setUsername(existingUser.getUsername());
            person.setFirstName(existingUser.getFirstName());
            person.setLastName(existingUser.getLastName());
            person.setEmail(existingUser.getEmail());
            person.setUserId(existingUser.getId());
            personRepository.save(person);
        }
    }

    public Long count() {
        return  this.userRepository.count();
    }
}

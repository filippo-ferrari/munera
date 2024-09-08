package com.application.munera;

import com.application.munera.data.Person;
import com.application.munera.repositories.PersonRepository;
import com.application.munera.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class StartupInitializer {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    @Autowired
    public StartupInitializer(UserRepository userRepository, PersonRepository personRepository) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    /**
     * Initializes Person entities for all existing users.
     */
    @PostConstruct
    public void initializePersonEntities() {
        userRepository.findAll().forEach(user -> {
            // Check if Person entity exists for the user by userId
            if (personRepository.findByUserId(user.getId()).isEmpty()) {
                // Create and save the Person entity
                Person newPerson = new Person();
                newPerson.setFirstName(user.getFirstName());
                newPerson.setLastName(user.getLastName());
                newPerson.setUsername(user.getUsername());
                newPerson.setUserId(user.getId());
                personRepository.save(newPerson);
            }
        });
    }
}

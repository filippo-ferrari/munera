package org.project.munera.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.project.munera.entities.Person;
import org.project.munera.repositories.PeopleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;

    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> fetchCreditors() {
        return this.peopleRepository.findAll();
    }

    public Person addNewCreditor(final @NonNull Person person) {
        return this.peopleRepository.save(person);
    }

    @Transactional
    public Person patchCreditor(@NonNull Person personToPatch, Person patchedPerson) {
        return peopleRepository.findById(personToPatch.getId())
                .map(existingPerson -> {
                    existingPerson = patchedPerson;
                    return peopleRepository.save(existingPerson);
                }).orElseThrow(() -> new EntityNotFoundException(("Creditor with ID " + personToPatch.getId() + "not found.")));
    }

    public void deleteCreditor(final @NonNull Person person) {
        this.peopleRepository.delete(person);
    }
}

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

    public List<Person> fetchPerson() {
        return this.peopleRepository.findAll();
    }

    public Person addNewPerson(final @NonNull Person person) {
        return this.peopleRepository.save(person);
    }

    @Transactional
    public Person patchPerson(@NonNull Person personToPatch, Person patchedPerson) {
        return peopleRepository.findById(personToPatch.getId())
                .map(existingPerson -> {
                    existingPerson = patchedPerson;
                    return peopleRepository.save(existingPerson);
                }).orElseThrow(() -> new EntityNotFoundException(("Person with ID " + personToPatch.getId() + "not found.")));
    }

    public void deletePerson(final @NonNull Person person) {
        this.peopleRepository.delete(person);
    }
}

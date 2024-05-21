package com.application.munera.services;

import com.application.munera.data.Person;
import com.application.munera.repositories.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> get(Long id) {
        return personRepository.findById(id);
    }

    public List<Person> findAll() {
        return this.personRepository.findAll();
    }

    public Person update(Person person) {
        return this.personRepository.save(person);
    }

    public void delete(Long id) {
        this.personRepository.deleteById(id);
    }

    public Page<Person> list(Pageable pageable, Specification<Person> filter) {
        return  this.personRepository.findAll(filter, pageable);
    }

    public int count() {
        return (int) this.personRepository.count();
    }
}

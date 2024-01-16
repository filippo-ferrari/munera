package org.project.munera.controllers;

import org.project.munera.entities.Person;
import org.project.munera.services.PeopleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PeopleController {

    private final PeopleService peopleService;

    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/people")
    public List<Person> list() {
        return this.peopleService.fetchPerson();
    }

    @GetMapping("/people/{person}")
    public Person reference(@PathVariable Person person) {
        return person;
    }

    @PostMapping("/people")
    public Person create(@RequestBody Person person) {
        return this.peopleService.addNewPerson(person);
    }

    @PatchMapping("/people/{person}")
    public Person patch(@PathVariable("person") Person personToPatch, @RequestBody Person personPatched) {
        return this.peopleService.patchPerson(personToPatch, personPatched);
    }

    @DeleteMapping("/people/{person}")
    public void delete(@PathVariable Person person) {
        this.peopleService.deletePerson(person);
    }
}

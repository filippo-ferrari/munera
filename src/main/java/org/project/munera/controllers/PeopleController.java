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

    @GetMapping("/creditors")
    public List<Person> list() {
        return this.peopleService.fetchCreditors();
    }

    @GetMapping("/creditors/{creditor}")
    public Person reference(@PathVariable Person creditor) {
        return creditor;
    }

    @PostMapping("/creditors")
    public Person create(@RequestBody Person creditor) {
        return this.peopleService.addNewCreditor(creditor);
    }

    @PatchMapping("/creditors/{creditor}")
    public Person patch(@PathVariable("creditor") Person creditorToPatch, @RequestBody Person creditorPatched) {
        return this.peopleService.patchCreditor(creditorToPatch, creditorPatched);
    }

    @DeleteMapping("/creditors/{creditor}")
    public void delete(@PathVariable Person creditor) {
        this.peopleService.deleteCreditor(creditor);
    }


}

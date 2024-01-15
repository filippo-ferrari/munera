package org.project.munera.repositories;

import org.project.munera.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PeopleRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
}

package com.application.munera.repositories;

import com.application.munera.data.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    Optional<Person> findByUserId(Long userId);

    @Query("SELECT p FROM Person p WHERE p.userId IS NULL")
    List<Person> findAllExcludeUser();
}

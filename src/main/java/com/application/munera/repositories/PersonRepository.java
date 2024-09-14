package com.application.munera.repositories;

import com.application.munera.data.Person;
import com.application.munera.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    Person findByUsername(String username);

    @Query("SELECT p FROM Person p WHERE p.username = :username")
    Optional<Person> findOptionalByUsername(@Param("username") String username);

    /**
     * finds all the people that the logged user has created, minus the person that represents the logged user
     * @param userId the logged user id, to get all people connected to id
     * @param username the logged username, to filter out
     * @return the list people found
     */
    @Query("SELECT p FROM Person p WHERE p.userId = :userId AND (p.username IS NULL OR p.username <> :username)")
    List<Person> findAllByUserIdExcludingPerson(@Param("userId") Long userId, @Param("username") String username);

    List<Person> findByUserId(Long userId);
}

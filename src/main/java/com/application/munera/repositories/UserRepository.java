package com.application.munera.repositories;

import com.application.munera.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(final String username);

    //TODO: join these two methods
    Optional<User> findOptionalByUsername(final String username);
}

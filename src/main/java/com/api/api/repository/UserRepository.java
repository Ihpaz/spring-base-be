package com.api.api.repository;

import com.api.api.entity.User;
import com.api.api.models.RegisterUserRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);

    @EntityGraph(attributePaths = {"role", "role.menus"})
    Optional<User> findByUsername(String username);
}

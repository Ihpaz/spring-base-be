package com.api.api.repository;

import com.api.api.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    boolean existsUserByUsername(String username);

    @EntityGraph(attributePaths = {"role", "role.menus"})
    Optional<User> findByUsername(String username);
}

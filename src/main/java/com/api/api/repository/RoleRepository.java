package com.api.api.repository;

import com.api.api.entity.Menu;
import com.api.api.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> , JpaSpecificationExecutor<Role> {
    Optional<Role> findByUuid(String uuid);

    @EntityGraph(attributePaths = {"roleMenu"})
    Page<Role> findAll(Specification<Role> specification, Pageable pageable);

}

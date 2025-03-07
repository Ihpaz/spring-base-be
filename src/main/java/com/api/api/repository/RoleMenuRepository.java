package com.api.api.repository;

import com.api.api.entity.Menu;
import com.api.api.entity.Role;
import com.api.api.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleMenuRepository extends JpaRepository<RoleMenu, Integer> , JpaSpecificationExecutor<RoleMenu> {
    Optional<RoleMenu> findByUuid(String uuid);

    void deleteByRole(Role existingRole);

    boolean existsByRoleAndMenu(Role newRole, Menu menu);
}

package com.api.api.repository;

import com.api.api.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Integer> , JpaSpecificationExecutor<Menu> {
    List<Menu> findByParentId(Integer parentId);

    Optional<Menu> findByUuid(String uuid);
}

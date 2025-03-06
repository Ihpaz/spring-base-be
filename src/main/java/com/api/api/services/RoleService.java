package com.api.api.services;

import com.api.api.entity.*;
import com.api.api.entity.Role;
import com.api.api.models.request.AddRoleRequest;
import com.api.api.models.request.RoleRequest;
import com.api.api.models.request.UpdateRoleRequest;
import com.api.api.models.responses.RoleMenuResponse;
import com.api.api.models.responses.RoleResponse;
import com.api.api.repository.MenuRepository;
import com.api.api.repository.RoleMenuRepository;
import com.api.api.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {


    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final MenuRepository menuRepository;

    public RoleService(RoleRepository roleRepository, MenuRepository menuRepository, RoleMenuRepository roleMenuRepository) {
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.roleMenuRepository = roleMenuRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    public Page<RoleResponse> getAllRoles(RoleRequest request) {
        Specification<Role> specification = ((root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getRole() != null) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + request.getRole() + "%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Role> Role = roleRepository.findAll(specification,pageable);
        List<RoleResponse> RoleResponses= Role.getContent().stream().map(this::toRoleResponse)
                .toList();

        return new PageImpl<>(RoleResponses, pageable, Role.getTotalElements());
    }


    public RoleResponse getRoleById(String uuid) {
        Optional<Role> Role = roleRepository.findByUuid(uuid);
        if (Role.isPresent()) {
            return toRoleResponse(Role.get());
        }else {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
    }


    public void createRole(AddRoleRequest request) {
        Role newRole = new Role();
        newRole.setUuid(UUID.randomUUID().toString());
        newRole.setRole(request.getRole());

        // Save Role first to generate its ID
        roleRepository.save(newRole);

        // Convert request roleMenu items to RoleMenu entities
        List<RoleMenu> roleMenus = request.getRoleMenu().stream().map(roleMenuRequest -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setUuid(UUID.randomUUID().toString());
            roleMenu.setRole(newRole);

            // Fetch the Menu entity using menuId
            Menu menu = menuRepository.findByUuid(roleMenuRequest.getMenuId())
                    .orElseThrow(() -> new RuntimeException("Menu not found"));

            roleMenu.setMenu(menu);
            roleMenu.set_created(roleMenuRequest.is_created());
            roleMenu.set_updated(roleMenuRequest.is_updated());
            roleMenu.set_deleted(roleMenuRequest.is_deleted());
            roleMenu.set_show(roleMenuRequest.is_show());

            return roleMenu;
        }).collect(Collectors.toList());

        // Save all RoleMenu entities
        roleMenuRepository.saveAll(roleMenus);
    }

    @Transactional
    public RoleResponse updateRole(String uuid, UpdateRoleRequest request) {

        // 1. Find the existing role by UUID
        Role existingRole = roleRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 2. Update Role details
        existingRole.setRole(request.getRole());

        // 3. Remove old RoleMenu records (if needed)
        roleMenuRepository.deleteByRole(existingRole); // Implement this in RoleMenuRepository

        // 4. Map new RoleMenu entries from request
        List<RoleMenu> newRoleMenus = request.getRoleMenu().stream().map(roleMenuRequest -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setUuid(UUID.randomUUID().toString());
            roleMenu.setRole(existingRole);

            // Find menu by UUID
            Menu menu = menuRepository.findByUuid(roleMenuRequest.getMenuId())
                    .orElseThrow(() -> new RuntimeException("Menu not found"));

            roleMenu.setMenu(menu);
            roleMenu.set_created(roleMenuRequest.is_created());
            roleMenu.set_updated(roleMenuRequest.is_updated());
            roleMenu.set_deleted(roleMenuRequest.is_deleted());
            roleMenu.set_show(roleMenuRequest.is_show());

            return roleMenu;
        }).collect(Collectors.toList());

        // 5. Save new RoleMenu records
        roleMenuRepository.saveAll(newRoleMenus);
        roleRepository.save(existingRole); // Save role updates

        return toRoleResponse(existingRole);
//        // 6. Convert to RoleResponse DTO
//        List<RoleMenuResponse> roleMenuResponses = newRoleMenus.stream().map(roleMenu ->
//                RoleMenuResponse.builder()
//                        .uuid(roleMenu.getUuid())
//                        .isShow(roleMenu.is_show())
//                        .isDeleted(roleMenu.is_deleted())
//                        .isCreated(roleMenu.is_created())
//                        .isUpdated(roleMenu.is_updated())
//                        .menuName(roleMenu.getMenu().getName())
//                        .menuIcon(roleMenu.getMenu().getIcon())
//                        .menuPath(roleMenu.getMenu().getPath())
//                        .build()
//        ).collect(Collectors.toList());
//
//        return RoleResponse.builder()
//                .uuid(existingRole.getUuid())
//                .role(existingRole.getRole())
//                .roleMenu(roleMenuResponses)
//                .build();
    }

    public void deleteRole(int id) {
        roleRepository.deleteById(id);
    }

    private RoleResponse toRoleResponse(Role role) {
        List<RoleMenuResponse> roleMenuResponses = role.getRoleMenu().stream().map(roleMenu ->
                RoleMenuResponse.builder()
                        .uuid(roleMenu.getUuid())
                        .isShow(roleMenu.is_show())
                        .isDeleted(roleMenu.is_deleted())
                        .isCreated(roleMenu.is_created())
                        .isUpdated(roleMenu.is_updated())
                        .menuName(roleMenu.getMenu().getName())
                        .menuIcon(roleMenu.getMenu().getIcon())
                        .menuPath(roleMenu.getMenu().getPath())
                        .build()
        ).collect(Collectors.toList());

        return RoleResponse.builder()
                .uuid(role.getUuid())
                .role(role.getRole())
                .roleMenu(roleMenuResponses)
                .build();
    }


}

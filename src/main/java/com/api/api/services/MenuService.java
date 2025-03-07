package com.api.api.services;

import com.api.api.entity.Menu;
import com.api.api.entity.Role;
import com.api.api.entity.User;
import com.api.api.models.request.AddMenuRequest;
import com.api.api.models.request.MenuRequest;
import com.api.api.models.request.UpdateMenuRequest;
import com.api.api.models.responses.MenuResponse;
import com.api.api.repository.MenuRepository;
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

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public Page<MenuResponse> getAllMenus(MenuRequest request) {
        Specification<Menu> specification = ((root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getName() != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }

            if (request.getParentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("parent_id"), request.getParentId()));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Menu> menu = menuRepository.findAll(specification,pageable);
        List<MenuResponse> menuResponses= menu.getContent().stream().map(this::toMenuResponse)
                .toList();

        return new PageImpl<>(menuResponses, pageable, menu.getTotalElements());
    }

    public MenuResponse getMenuById(String uuid) {
        Optional<Menu> menu = menuRepository.findByUuid(uuid);
        if (menu.isPresent()) {
            return toMenuResponse(menu.get());
        }else {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found");
        }
    }

//    public List<Menu> getSubMenus(int parentId) {
//        return menuRepository.findByParentId(parentId);
//    }

    @Transactional
    public void createMenu(AddMenuRequest menu) {
        Menu newMenu = new Menu();
        Menu parentMenu = null;
        if(menu.getParentId() != null) {
            parentMenu = menuRepository.findById(menu.getParentId()).orElse(null);
        }
        newMenu.setName(menu.getName());
        newMenu.setIcon(menu.getIcon());
        newMenu.setPath(menu.getPath());
        newMenu.setPriority(menu.getPriority());
        newMenu.setParent(parentMenu);
        newMenu.setUuid(UUID.randomUUID().toString());

        menuRepository.save(newMenu);
    }

    @Transactional
    public Menu updateMenu(String uuid, UpdateMenuRequest request) {

        return menuRepository.findByUuid(uuid).map(menu -> {

            if(request.getName()!= null) menu.setName(request.getName());
            if(request.getPath()!= null) menu.setPath(request.getPath());
            if(request.getIcon()!= null) menu.setIcon(request.getIcon());
            if(request.getPriority()!= null) menu.setPriority(request.getPriority());

            Menu parentMenu = null;
            if(request.getParentId() != null) {
                parentMenu = menuRepository.findById(request.getParentId())
                        .orElseThrow(() -> new RuntimeException("Menu Parent not found with id " + request.getParentId()));
                menu.setParent(parentMenu);
            }

            return menuRepository.save(menu);
        }).orElseThrow(() -> new RuntimeException("Menu not found with id " + uuid));
    }

    public void deleteMenu(int id) {
        menuRepository.deleteById(id);
    }

    private MenuResponse toMenuResponse(Menu menu) {
        return MenuResponse.builder()
                .name(menu.getName())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .uuid(menu.getUuid())
                .priority(menu.getPriority())
                .parentId(menu.getParent() != null ? menu.getParent().getId() : null)
                .build();
    }
}

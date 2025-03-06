package com.api.api.controllers;

import com.api.api.entity.Menu;
import com.api.api.models.request.AddMenuRequest;
import com.api.api.models.request.MenuRequest;
import com.api.api.models.request.UpdateMenuRequest;
import com.api.api.models.responses.MenuResponse;
import com.api.api.models.responses.PagingResponse;
import com.api.api.models.responses.WebResponse;
import com.api.api.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public WebResponse<List<MenuResponse>> getAllMenus(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Integer parentId) {
        MenuRequest request = MenuRequest.builder().name(name).parentId(parentId).build();

        Page<MenuResponse> menuResponse = menuService.getAllMenus(request);
        return WebResponse.<List<MenuResponse>>builder()
                .data(menuResponse.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(menuResponse.getNumber())
                        .totalPage(menuResponse.getTotalPages())
                        .size(menuResponse.getSize())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public WebResponse<MenuResponse> getMenuById(@PathVariable String uuid) {
        MenuResponse menu = menuService.getMenuById(uuid);
        return WebResponse.<MenuResponse>builder().data(menu).build();
    }
//
//    @GetMapping("/parent/{parentId}")
//    public WebResponse<MenuResponse> getSubMenus(@PathVariable int parentId) {
//        return menuService.getSubMenus(parentId);
//    }

    @PostMapping
    public WebResponse<String> createMenu(@RequestBody AddMenuRequest request) {
        try {
            AddMenuRequest menuRequest = AddMenuRequest.builder()
                    .name(request.getName())
                    .path(request.getPath())
                    .icon(request.getIcon())
                    .priority(request.getPriority())
                    .parentId(request.getParentId())
                    .build();

        menuService.createMenu(menuRequest);
        return WebResponse.<String>builder().data("Menu created").build();

        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create menu", e);
        }
    }

    @PutMapping("/{uuid}")
    public WebResponse<MenuResponse> updateMenu(@PathVariable String uuid, @RequestBody UpdateMenuRequest request) {
        try {

            UpdateMenuRequest updateMenuRequest = UpdateMenuRequest.builder()
                    .name(request.getName())
                    .path(request.getPath())
                    .icon(request.getIcon())
                    .priority(request.getPriority())
                    .parentId(request.getParentId())
                    .build();

            Menu updatedMenu = menuService.updateMenu(uuid, updateMenuRequest);
            MenuResponse menuResponse = MenuResponse.builder()
                    .icon(updatedMenu.getIcon())
                    .name(updatedMenu.getName())
                    .path(updatedMenu.getPath())
                    .priority(updatedMenu.getPriority())
                    .parentId(updatedMenu.getParent().getId())
                    .build();

            return WebResponse.<MenuResponse>builder().data(menuResponse).build();

        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update menu", e);
        }
    }

    @DeleteMapping("/{id}")
    public WebResponse<Void> deleteMenu(@PathVariable int id) {
        menuService.deleteMenu(id);
        return WebResponse.<Void>builder().build();
    }
}

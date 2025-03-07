package com.api.api.controllers;

import com.api.api.entity.Menu;
import com.api.api.models.request.AddMenuRequest;
import com.api.api.models.request.MenuRequest;
import com.api.api.models.request.UpdateMenuRequest;
import com.api.api.models.responses.MenuResponse;
import com.api.api.models.responses.PagingResponse;
import com.api.api.models.responses.WebResponse;
import com.api.api.services.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@Validated
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public WebResponse<List<MenuResponse>> getAllMenus(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Integer parentId,@RequestParam(value = "username", required = false) String username,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        MenuRequest request = MenuRequest.builder()
                .name(name)
                .parentId(parentId)
                .page(page)
                .size(size)
                .build();

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

    @GetMapping("/{uuid}")
    public WebResponse<MenuResponse> getMenuById(@PathVariable String uuid) {
        MenuResponse menu = menuService.getMenuById(uuid);
        return WebResponse.<MenuResponse>builder().data(menu).build();
    }
//
//    @GetMapping("/parent/{parentId}")
//    public WebResponse<MenuResponse> getSubMenus(@PathVariable int parentId) {
//        return menuService.getSubMenus(parentId);
//    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> createMenu(@Valid @RequestBody AddMenuRequest request) {
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

    @PutMapping(value="/{uuid}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<MenuResponse> updateMenu(@Valid @PathVariable String uuid, @RequestBody UpdateMenuRequest request) {
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

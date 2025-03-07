package com.api.api.controllers;


import com.api.api.models.request.*;
import com.api.api.models.responses.RoleResponse;
import com.api.api.models.responses.PagingResponse;
import com.api.api.models.responses.WebResponse;
import com.api.api.services.RoleService;
import com.api.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {


    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public WebResponse<List<RoleResponse>> getAllRoles(@RequestParam(required = false) String role,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        RoleRequest request = RoleRequest.builder().role(role).build();

        Page<RoleResponse> RoleResponse = roleService.getAllRoles(request);
        return WebResponse.<List<RoleResponse>>builder()
                .data(RoleResponse.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(RoleResponse.getNumber())
                        .totalPage(RoleResponse.getTotalPages())
                        .size(RoleResponse.getSize())
                        .build())
                .build();
    }

    @GetMapping("/{uuid}")
    public WebResponse<RoleResponse> getMenuById(@PathVariable String uuid) {
        RoleResponse menu = roleService.getRoleById(uuid);
        return WebResponse.<RoleResponse>builder().data(menu).build();
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> createRole(@Valid @RequestBody AddRoleRequest request) {
        try {
            List<RoleMenuRequest> roleMenus = request.getRoleMenu() != null
                    ? request.getRoleMenu().stream()
                    .map(roleMenu -> RoleMenuRequest.builder()
                            .menuId(roleMenu.getMenuId())
                            .is_show(roleMenu.is_show())
                            .is_deleted(roleMenu.is_deleted())
                            .is_created(roleMenu.is_created())
                            .is_updated(roleMenu.is_updated())
                            .build())
                    .collect(Collectors.toList())
                    : null;

            AddRoleRequest RoleRequest = AddRoleRequest.builder()
                    .role(request.getRole())
                    .roleMenu(roleMenus)
                    .build();

            roleService.createRole(RoleRequest);

            return WebResponse.<String>builder().data("Role created").build();

        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create role", e);
        }
    }

    @PutMapping(value = "/{uuid}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<RoleResponse> updateMenu(@Valid @PathVariable String uuid, @RequestBody UpdateRoleRequest request) {
        try {

            List<RoleMenuRequest> roleMenus = request.getRoleMenu() != null
                    ? request.getRoleMenu().stream()
                    .map(roleMenu -> RoleMenuRequest.builder()
                            .menuId(roleMenu.getMenuId())
                            .is_show(roleMenu.is_show())
                            .is_deleted(roleMenu.is_deleted())
                            .is_created(roleMenu.is_created())
                            .is_updated(roleMenu.is_updated())
                            .build())
                    .collect(Collectors.toList())
                    : null;

            UpdateRoleRequest RoleRequest = UpdateRoleRequest.builder()
                    .role(request.getRole())
                    .roleMenu(roleMenus)
                    .build();


            RoleResponse updatedRole = roleService.updateRole(uuid, RoleRequest);


            return WebResponse.<RoleResponse>builder().data(updatedRole).build();

        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update menu", e);
        }
    }

    @DeleteMapping("/{id}")
    public WebResponse<Void> deleteMenu(@PathVariable int id) {
        roleService.deleteRole(id);
        return WebResponse.<Void>builder().build();
    }
}

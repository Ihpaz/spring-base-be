package com.api.api.controllers;

import com.api.api.entity.User;
import com.api.api.models.request.RegisterUserRequest;
import com.api.api.models.request.RoleRequest;
import com.api.api.models.request.SearchUserRequest;
import com.api.api.models.responses.PagingResponse;
import com.api.api.models.responses.RoleResponse;
import com.api.api.models.responses.UserResponse;
import com.api.api.models.responses.WebResponse;
import com.api.api.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/user")
@RestController
public class UserController {

    private UserService userService;

    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request){
            userService.registerUser(request);

            return  WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/me"
    )
    public WebResponse<UserResponse> getUserWithRoleAndMenus(User currentUser){
        try {
            UserResponse userResponse = userService.getUserWithRoleAndMenus(currentUser);
            return WebResponse.<UserResponse>builder().data(userResponse).build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch user data", e);
        }
    }

    @PatchMapping(
        path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> updateUser(@RequestBody RegisterUserRequest request){
        UserResponse userResponse= userService.updateUser(request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @GetMapping
    public WebResponse<List<UserResponse>> getUsers(@RequestParam(value = "username", required = false) String username,
                                                    @RequestParam(value = "role", required = false) String role,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        SearchUserRequest request = SearchUserRequest.builder()
                .role(role)
                .username(username)
                .page(page)
                .size(size)
                .build();

        Page<UserResponse> UserResponse = userService.getUsers(request);

        return WebResponse.<List<UserResponse>>builder()
                .data(UserResponse.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(UserResponse.getNumber())
                        .totalPage(UserResponse.getTotalPages())
                        .size(UserResponse.getSize())
                        .build())
                .build();
    }
}

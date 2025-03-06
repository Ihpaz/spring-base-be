package com.api.api.controllers;

import com.api.api.entity.User;
import com.api.api.models.request.RegisterUserRequest;
import com.api.api.models.responses.UserResponse;
import com.api.api.models.responses.WebResponse;
import com.api.api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
}

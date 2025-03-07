package com.api.api.controllers;

import com.api.api.entity.User;
import com.api.api.models.request.LoginUserRequest;
import com.api.api.models.request.RegisterUserRequest;
import com.api.api.models.responses.LoginResponse;
import com.api.api.models.responses.WebResponse;
import com.api.api.services.AuthService;
import com.api.api.services.JwtService;
import com.api.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;
    private final UserService userService;
    // ✅ Use constructor injection for dependencies
    public AuthController(JwtService jwtService, AuthService authService,UserService userService) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping(value = "/signup",consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register(@Valid @RequestBody RegisterUserRequest registerUserDto) {
        System.out.println(registerUserDto);
        userService.registerUser(registerUserDto);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PostMapping("/login")
    public WebResponse<LoginResponse> authenticate(@Valid @RequestBody LoginUserRequest loginUserDto) {
        User authenticatedUser = authService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return WebResponse.<LoginResponse>builder().data(loginResponse).build();
    }
}

package com.api.api.controllers;

import com.api.api.entity.User;
import com.api.api.models.request.LoginUserRequest;
import com.api.api.models.request.RegisterUserRequest;
import com.api.api.models.responses.LoginResponse;
import com.api.api.models.responses.WebResponse;
import com.api.api.services.AuthService;
import com.api.api.services.JwtService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    // ✅ Use constructor injection for dependencies
    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping(value = "/signup",consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register(@RequestBody RegisterUserRequest registerUserDto) {
            authService.signup(registerUserDto);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PostMapping("/login")
    public WebResponse<LoginResponse> authenticate(@RequestBody LoginUserRequest loginUserDto) {
        User authenticatedUser = authService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return WebResponse.<LoginResponse>builder().data(loginResponse).build();
    }
}

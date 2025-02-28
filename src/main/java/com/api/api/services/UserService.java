package com.api.api.services;

import com.api.api.entity.User;
import com.api.api.models.RegisterUserRequest;
import com.api.api.models.SearchUserRequest;
import com.api.api.models.UserResponse;
import com.api.api.repository.UserRepository;
import com.api.api.security.BCrypt;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.data.domain.Pageable;

import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private  UserRepository userRepository;

    @Transactional
    void registerUser(RegisterUserRequest user) {
        if(userRepository.existsUserByUsername(user.getUsername())) {
            log.info("User already exists with username {}", user.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(newUser);
    }


    public UserResponse getUserWithRoleAndMenus(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .username(user.getUsername())
                .role(user.getRole().getRole())
                .menus(user.getRole().getRoleMenu().stream()
                        .map(menu -> "ss")
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    void updateUser(RegisterUserRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
    }

    public Page<UserResponse> getUsers(Pageable pageable) {

        Page<User> user = userRepository.findAll(pageable);
        List<UserResponse> userResponses= user.getContent().stream().map(this::toUserResponse)
                .toList();

        return new PageImpl<>(userResponses, pageable, user.getTotalElements());
    }


    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .role(user.getRole().getRole())
                .build();
    }

    private page<UserResponse> searchUsers(SearchUserRequest searchUserRequest){

    }
}

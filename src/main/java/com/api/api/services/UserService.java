package com.api.api.services;

import com.api.api.entity.Role;
import com.api.api.entity.User;
import com.api.api.models.request.RegisterUserRequest;
import com.api.api.models.request.SearchUserRequest;
import com.api.api.models.request.UpdateUserRequest;
import com.api.api.models.responses.UserResponse;
import com.api.api.repository.UserRepository;
import com.api.api.security.BCrypt;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUser(RegisterUserRequest user) {
        try {
            if(userRepository.existsUserByUsername(user.getUsername())) {
                log.info("User already exists with username {}", user.getUsername());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
            }
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setUuid(UUID.randomUUID().toString());
            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            newUser.setAvatar("https://www.google.com/images/branding/google_logo.png");

            userRepository.save(newUser);
        }  catch (Exception e) {
            throw new RuntimeException("User creation failed", e); // Will be caught by GlobalExceptionHandler
        }
    }


    public UserResponse getUserWithRoleAndMenus(User user) {
        User userQuery = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .username(userQuery.getUsername())
                .role(userQuery.getRole().getRole())
                .menus(userQuery.getRole().getRoleMenu().stream()
                        .map(menu -> "ss")
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public UserResponse updateUser(String uuid, UpdateUserRequest request) {
       log.info("request ya",request);

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

//        user.setAvatar("https://www.google.com/images/branding/google_logo.png");

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getRole())
                .uuid(user.getUuid())
                .build();

    }

    public Page<UserResponse> getUsers(SearchUserRequest searchUserRequest) {
        Specification<User> specification = ((root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (searchUserRequest.getUsername() != null) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + searchUserRequest.getUsername() + "%"));
            }

            if (searchUserRequest.getRole() != null) {
                Join<User, Role> roleJoin = root.join("role"); // Join role table
                predicates.add(criteriaBuilder.like(roleJoin.get("role"), "%" + searchUserRequest.getRole() + "%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        Pageable pageable = PageRequest.of(searchUserRequest.getPage(), searchUserRequest.getSize());
        Page<User> user = userRepository.findAll(specification,pageable);
        List<UserResponse> userResponses= user.getContent().stream().map(this::toUserResponse)
                .toList();

        return new PageImpl<>(userResponses, pageable, user.getTotalElements());
    }


    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .uuid(user.getUuid())
                .role(user.getRole() != null ? user.getRole().getRole() : null)
                .build();
    }

    public Page<UserResponse> searchUsers(SearchUserRequest searchUserRequest){
            Specification<User> specification = ((root, query, criteriaBuilder) ->
            {
                List<Predicate> predicates = new ArrayList<>();
                if (searchUserRequest.getUsername() != null) {
                    predicates.add(criteriaBuilder.like(root.get("username"), "%" + searchUserRequest.getUsername() + "%"));
                }

                if (searchUserRequest.getRole() != null) {
                    Join<User, Role> roleJoin = root.join("role"); // Join role table
                    predicates.add(criteriaBuilder.like(roleJoin.get("role"), "%" + searchUserRequest.getRole() + "%"));
                }

                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            });

        Pageable pageable = PageRequest.of(searchUserRequest.getPage(), searchUserRequest.getSize());
        Page<User> users = userRepository.findAll(specification, pageable);
        List<UserResponse> userResponses = users.getContent().stream()
                .map(this::toUserResponse)
                .toList();

        return new PageImpl<>(userResponses, pageable, users.getTotalElements());

    }
}

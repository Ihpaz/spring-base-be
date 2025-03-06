package com.api.api.services;

import com.api.api.entity.Role;
import com.api.api.entity.User;
import com.api.api.models.request.RegisterUserRequest;
import com.api.api.models.request.SearchUserRequest;
import com.api.api.models.responses.UserResponse;
import com.api.api.repository.UserRepository;
import com.api.api.security.BCrypt;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
        if(userRepository.existsUserByUsername(user.getUsername())) {
            log.info("User already exists with username {}", user.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(newUser);
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
    public UserResponse updateUser(RegisterUserRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .role(user.getRole().getRole())
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
                .role(user.getRole().getRole())
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

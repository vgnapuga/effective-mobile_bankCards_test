package com.example.bankcards.controller.user;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.controller.BaseController;
import com.example.bankcards.dto.user.request.UserCreateRequest;
import com.example.bankcards.dto.user.response.UserListResponse;
import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.model.user.User;
import com.example.bankcards.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public final class AdminUserController extends BaseController {

    private static final String ROOT = "/api/admin/users";

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody final UserCreateRequest request,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("POST(id={}) - {}", adminId, ROOT);

        User createdUser = userService.createUser(adminId, request);
        UserResponse response = UserResponse.of(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable final Long userId, final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("GET(id={}) - {}/{}", adminId, ROOT, userId);

        User retrievedUser = userService.getUserByIdForAdmin(adminId, userId);
        UserResponse response = UserResponse.of(retrievedUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(defaultValue = "id") final String sortBy,
            @RequestParam(defaultValue = "asc") final String sortDirection,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("GET(id={}) - {}", adminId, ROOT);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userService.getAllUsers(adminId, pageable);
        UserListResponse response = new UserListResponse(
                userPage.getContent().stream().map(UserResponse::of).toList(),
                userPage.getTotalElements(),
                page,
                size);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable final Long userId, Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("DELETE(id={}) - {}/{}", adminId, ROOT, userId);

        userService.deleteUserById(adminId, userId);

        return ResponseEntity.noContent().build();
    }

}

package com.example.bankcards.controller.user;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.controller.BaseController;
import com.example.bankcards.dto.user.request.UserUpdateEmailRequest;
import com.example.bankcards.dto.user.request.UserUpdatePasswordRequest;
import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.model.user.User;
import com.example.bankcards.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public final class UserController extends BaseController {

    private final UserService userService;

    @PutMapping("/email")
    public ResponseEntity<UserResponse> updateUserEmail(
            @Valid @RequestBody UserUpdateEmailRequest request,
            final Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        User updatedUser = userService.updateUserEmail(userId, request);
        UserResponse response = UserResponse.of(updatedUser);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<UserResponse> updateUserPassword(
            @Valid @RequestBody final UserUpdatePasswordRequest request,
            final Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        User updatedUser = userService.updateUserPassword(userId, request);
        UserResponse response = UserResponse.of(updatedUser);

        return ResponseEntity.ok(response);
    }

}

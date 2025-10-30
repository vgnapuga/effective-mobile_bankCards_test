package com.example.bankcards.dto.user.response;


import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.bankcards.model.user.User;
import com.example.bankcards.util.constant.UserConstants;


public record UserResponse(Long id, String email, Set<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static UserResponse of(final User user) {
        User nonNullUser = Objects.requireNonNull(user, UserConstants.DTO_REQUIRED_MESSAGE);

        String emailValue = nonNullUser.getEmail().getValue();
        Set<String> roleNames = nonNullUser.getRoles().stream().map(role -> role.toString()).collect(
                Collectors.toSet());

        return new UserResponse(
                nonNullUser.getId(),
                emailValue,
                roleNames,
                nonNullUser.getCreationTime(),
                nonNullUser.getUpdateTime());
    }

}

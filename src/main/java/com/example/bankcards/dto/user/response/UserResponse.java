package com.example.bankcards.dto.user.response;


import java.time.LocalDateTime;
import java.util.Objects;

import com.example.bankcards.model.user.User;
import com.example.bankcards.util.constant.UserConstants;


public record UserResponse(Long id, String email, String role, LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static UserResponse of(final User user) {
        User nonNullUser = Objects.requireNonNull(user, UserConstants.DTO_REQUIRED_MESSAGE);
        return new UserResponse(
                nonNullUser.getId(),
                nonNullUser.getEmail().getValue(),
                nonNullUser.getRole().toString(),
                nonNullUser.getCreationTime(),
                nonNullUser.getUpdateTime());
    }

}

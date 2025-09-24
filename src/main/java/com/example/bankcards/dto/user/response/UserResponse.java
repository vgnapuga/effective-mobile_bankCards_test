package com.example.bankcards.dto.user.response;


import java.time.LocalDateTime;
import java.util.Set;


public record UserResponse(Long id, String email, Set<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
}

package com.example.bankcards.dto.auth.response;


public record LoginResponse(String token, Long userId, long expiresIn) {
}

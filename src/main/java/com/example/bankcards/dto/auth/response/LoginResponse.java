package com.example.bankcards.dto.auth.response;


public record LoginResponse(String token, Long userId, long expiresIn) {

    public static LoginResponse of(final String token, final Long userId, final long expiresIn) {
        return new LoginResponse(token, userId, expiresIn);
    }

}

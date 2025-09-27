package com.example.bankcards.dto.auth.response;


import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Ответ после успешной авторизации")
public record LoginResponse(
        @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1NiIs...") String token,
        @Schema(description = "ID пользователя", example = "1") Long userId,
        @Schema(description = "Время истечения токена в ms", example = "3600000") long expiresIn) {

    public static LoginResponse of(final String token, final Long userId, final long expiresIn) {
        return new LoginResponse(token, userId, expiresIn);
    }

}

package com.example.bankcards.dto.auth.request;


import com.example.bankcards.dto.validation.user.ValidEmail;
import com.example.bankcards.dto.validation.user.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Запрос на авторизацию")
public record LoginRequest(
        @Schema(description = "Email пользователя", example = "test@example.com") @ValidEmail String email,
        @Schema(description = "Пароль пользователя", example = "password123") @ValidPassword String password) {
}

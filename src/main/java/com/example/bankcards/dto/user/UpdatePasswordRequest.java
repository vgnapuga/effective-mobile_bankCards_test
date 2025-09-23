package com.example.bankcards.dto.user;


import com.example.bankcards.util.constant.UserConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UpdatePasswordRequest(
        @NotBlank(message = UserConstants.Password.DTO_REQUIRED_MESSAGE) @Size(min = UserConstants.Password.RAW_PASSWORD_MIN_SIZE, message = UserConstants.Password.DTO_INVALID_LENGTH_MESSAGE) String password) {
}

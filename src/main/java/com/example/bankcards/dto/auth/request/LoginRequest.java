package com.example.bankcards.dto.auth.request;


import com.example.bankcards.dto.validation.user.ValidEmail;
import com.example.bankcards.dto.validation.user.ValidPassword;


public record LoginRequest(@ValidEmail String email, @ValidPassword String password) {
}

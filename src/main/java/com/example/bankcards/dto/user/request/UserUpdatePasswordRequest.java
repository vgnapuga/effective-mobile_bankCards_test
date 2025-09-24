package com.example.bankcards.dto.user.request;


import com.example.bankcards.dto.validation.user.ValidPassword;


public record UserUpdatePasswordRequest(@ValidPassword String password) {
}

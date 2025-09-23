package com.example.bankcards.dto.user;


import com.example.bankcards.dto.validation.ValidPassword;


public record UpdatePasswordRequest(@ValidPassword String password) {
}

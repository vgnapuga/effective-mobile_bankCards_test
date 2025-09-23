package com.example.bankcards.dto.user;


import com.example.bankcards.dto.validation.ValidEmail;
import com.example.bankcards.dto.validation.ValidPassword;


public record CreateRequest(@ValidEmail String email, @ValidPassword String password) {
}

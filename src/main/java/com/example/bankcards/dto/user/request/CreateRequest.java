package com.example.bankcards.dto.user.request;


import com.example.bankcards.dto.validation.user.ValidEmail;
import com.example.bankcards.dto.validation.user.ValidPassword;


public record CreateRequest(@ValidEmail String email, @ValidPassword String password) {
}

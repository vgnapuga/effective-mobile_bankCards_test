package com.example.bankcards.dto.user.request;


import com.example.bankcards.dto.validation.user.ValidEmail;


public record UserUpdateEmailRequest(@ValidEmail String email) {
}

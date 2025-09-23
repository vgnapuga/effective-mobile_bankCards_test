package com.example.bankcards.dto.user;


import com.example.bankcards.dto.validation.ValidEmail;


public record UpdateEmailRequest(@ValidEmail String email) {
}

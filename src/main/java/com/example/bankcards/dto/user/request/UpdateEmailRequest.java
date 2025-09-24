package com.example.bankcards.dto.user.request;


import com.example.bankcards.dto.validation.user.ValidEmail;


public record UpdateEmailRequest(@ValidEmail String email) {
}

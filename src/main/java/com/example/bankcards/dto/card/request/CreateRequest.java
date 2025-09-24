package com.example.bankcards.dto.card.request;


import java.time.LocalDate;

import com.example.bankcards.dto.validation.card.ValidCardExpiryDate;
import com.example.bankcards.dto.validation.card.ValidCardNumber;
import com.example.bankcards.dto.validation.card.ValidCardOwnerId;


public record CreateRequest(
        @ValidCardNumber String cardNumber,
        @ValidCardOwnerId Long ownerId,
        @ValidCardExpiryDate LocalDate expiryDate) {
}
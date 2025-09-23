package com.example.bankcards.dto.card;


import java.time.LocalDate;

import com.example.bankcards.dto.validation.ValidCardExpiryDate;
import com.example.bankcards.dto.validation.ValidCardNumber;
import com.example.bankcards.dto.validation.ValidCardOwnerId;


public record CreateRequest(
        @ValidCardNumber String cardNumber,
        @ValidCardOwnerId Long ownerId,
        @ValidCardExpiryDate LocalDate expiryDate) {
}
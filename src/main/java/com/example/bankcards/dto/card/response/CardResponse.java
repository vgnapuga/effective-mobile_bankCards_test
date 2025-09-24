package com.example.bankcards.dto.card.response;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


public record CardResponse(
        Long id,
        String last4,
        String status,
        LocalDate expiryDate,
        BigDecimal balance,
        LocalDateTime createdAt) {
}

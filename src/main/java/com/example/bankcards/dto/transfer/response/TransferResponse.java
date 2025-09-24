package com.example.bankcards.dto.transfer.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TransferResponse(
        Long id,
        String fromCardLast4,
        String toCardLast4,
        BigDecimal amount,
        LocalDateTime timestamp) {
}

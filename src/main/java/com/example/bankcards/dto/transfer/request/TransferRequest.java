package com.example.bankcards.dto.transfer.request;


import java.math.BigDecimal;
import java.util.Set;

import com.example.bankcards.dto.validation.card.ValidCardId;
import com.example.bankcards.dto.validation.transfer.ValidTransferAmount;


public record TransferRequest(
        @ValidCardId Long fromCardId,
        @ValidCardId Long toCardId,
        @ValidTransferAmount BigDecimal amount,
        Set<Long> categoryIds) {
}

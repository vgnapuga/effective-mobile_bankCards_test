package com.example.bankcards.dto.transfer.request;


import java.math.BigDecimal;

import com.example.bankcards.dto.validation.card.ValidCardId;
import com.example.bankcards.dto.validation.transfer.ValidTransferAmount;


public record TransferRequest(
        @ValidCardId Long fromCardId,
        @ValidCardId Long toCardId,
        @ValidTransferAmount BigDecimal amount) {
}

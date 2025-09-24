package com.example.bankcards.dto.transfer.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.util.constant.TransferConstants;


public record TransferResponse(
        Long id,
        String fromCardLast4,
        String toCardLast4,
        BigDecimal amount,
        LocalDateTime timestamp) {

    public static TransferResponse of(final Transfer transfer) {
        Transfer nonNullTransfer = Objects.requireNonNull(transfer, TransferConstants.DTO_REQUIRED_MESSAGE);

        String fromCardLast4 = nonNullTransfer.getFromCard().getLast4();
        String toCardLast4 = nonNullTransfer.getToCard().getLast4();
        BigDecimal amountValue = nonNullTransfer.getAmount().getValue();

        return new TransferResponse(
                transfer.getId(),
                fromCardLast4,
                toCardLast4,
                amountValue,
                transfer.getCreationTime());
    }

}

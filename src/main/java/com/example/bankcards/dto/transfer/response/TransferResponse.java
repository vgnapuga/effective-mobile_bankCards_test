package com.example.bankcards.dto.transfer.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.util.constant.TransferConstants;


public record TransferResponse(
        Long id,
        String fromCardLast4,
        String toCardLast4,
        BigDecimal amount,
        Set<String> categories,
        LocalDateTime timestamp) {

    public static TransferResponse of(final Transfer transfer) {
        Transfer nonNullTransfer = Objects.requireNonNull(transfer, TransferConstants.DTO_REQUIRED_MESSAGE);

        String fromCardLast4 = nonNullTransfer.getFromCard().getLast4();
        String toCardLast4 = nonNullTransfer.getToCard().getLast4();
        BigDecimal amountValue = nonNullTransfer.getAmount().getValue();
        Set<String> categoryNames = nonNullTransfer.getCategories().stream().map(
                category -> category.getName().toString()).collect(Collectors.toSet());

        return new TransferResponse(
                transfer.getId(),
                fromCardLast4,
                toCardLast4,
                amountValue,
                categoryNames,
                transfer.getCreationTime());
    }

}

package com.example.bankcards.dto.card.response;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.example.bankcards.model.card.Card;
import com.example.bankcards.util.constant.CardConstants;


public record CardResponse(
        Long id,
        String last4,
        String status,
        LocalDate expiryDate,
        BigDecimal balance,
        LocalDateTime createdAt) {

    public static CardResponse of(final Card card) {
        Card nonNullCard = Objects.requireNonNull(card, CardConstants.DTO_REQUIRED_MESSAGE);

        String statusName = nonNullCard.getStatus().toString();
        LocalDate expiryDateValue = nonNullCard.getExpiryDate().getValue();
        BigDecimal balanceValue = nonNullCard.getBalance().getValue();

        return new CardResponse(
                nonNullCard.getId(),
                nonNullCard.getLast4(),
                statusName,
                expiryDateValue,
                balanceValue,
                nonNullCard.getCreationTime());
    }

}

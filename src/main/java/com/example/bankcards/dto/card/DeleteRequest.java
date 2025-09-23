package com.example.bankcards.dto.card;


import com.example.bankcards.util.constant.CardConstants;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record DeleteRequest(
        @NotNull(message = CardConstants.DTO_REQUIRED_ID_MESSAGE) @Positive(message = CardConstants.DTO_NEGATIVE_ID_MESSAGE) Long cardId) {
}

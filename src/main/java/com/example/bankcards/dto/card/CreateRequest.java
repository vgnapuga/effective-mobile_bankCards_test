package com.example.bankcards.dto.card;


import java.time.LocalDate;

import com.example.bankcards.util.constant.CardConstants;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;


public record CreateRequest(
        @NotBlank(message = CardConstants.CardNumber.DTO_REQUIRED_MESSAGE) @Pattern(regexp = CardConstants.CardNumber.CARD_REGEX, message = CardConstants.CardNumber.DTO_INVALID_FORMAT_MESSAGE) String cardNumber,
        @NotNull(message = CardConstants.CardOwner.DTO_REQUIRED_MESSAGE) @Positive(message = CardConstants.CardOwner.DTO_NEGATIVE_ID_MESSAGE) Long ownerId,
        @NotNull(message = CardConstants.CardExpiryDate.DTO_REQUIRED_MESSAGE) @FutureOrPresent(message = CardConstants.CardExpiryDate.DTO_PAST_DATE_MESSAGE) LocalDate expiryDate) {
}
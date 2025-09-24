package com.example.bankcards.dto.validation.card;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.bankcards.util.constant.CardConstants;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = CardConstants.CardOwner.DTO_REQUIRED_MESSAGE)
@Positive(message = CardConstants.CardOwner.DTO_NEGATIVE_ID_MESSAGE)
public @interface ValidCardOwnerId {
}

package com.example.bankcards.dto.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.bankcards.util.constant.CardConstants;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = CardConstants.CardExpiryDate.DTO_REQUIRED_MESSAGE)
@FutureOrPresent(message = CardConstants.CardExpiryDate.DTO_PAST_DATE_MESSAGE)
public @interface ValidCardExpiryDate {
}

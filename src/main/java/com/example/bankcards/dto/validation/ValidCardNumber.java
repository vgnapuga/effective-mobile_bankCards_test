package com.example.bankcards.dto.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.bankcards.util.constant.CardConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = CardConstants.CardNumber.DTO_REQUIRED_MESSAGE)
@Pattern(regexp = CardConstants.CardNumber.CARD_REGEX, message = CardConstants.CardNumber.DTO_INVALID_FORMAT_MESSAGE)
public @interface ValidCardNumber {
}

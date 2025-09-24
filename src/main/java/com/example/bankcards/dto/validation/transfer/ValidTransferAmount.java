package com.example.bankcards.dto.validation.transfer;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.bankcards.util.constant.CardConstants;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = CardConstants.TransferAmount.DTO_REQUIRED_MESSAGE)
@DecimalMin(value = CardConstants.TransferAmount.MIN_VALUE, message = CardConstants.TransferAmount.DTO_NEGATIVE_OR_ZERO_MESSAGE)
@Digits(integer = CardConstants.TransferAmount.INTEGER_VALUE, fraction = CardConstants.TransferAmount.FRACTION_VALUE, message = CardConstants.TransferAmount.DTO_INVALID_FORMAT_MESSAGE)
public @interface ValidTransferAmount {

}

package com.example.bankcards.dto.validation.transfer;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.bankcards.util.constant.TransferConstants;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = TransferConstants.Amount.DTO_REQUIRED_MESSAGE)
@DecimalMin(value = TransferConstants.Amount.MIN_VALUE_STRING, message = TransferConstants.Amount.DTO_NEGATIVE_OR_ZERO_MESSAGE)
@Digits(integer = TransferConstants.Amount.INTEGER_VALUE, fraction = TransferConstants.Amount.FRACTION_VALUE, message = TransferConstants.Amount.DTO_INVALID_FORMAT_MESSAGE)
public @interface ValidTransferAmount {

}

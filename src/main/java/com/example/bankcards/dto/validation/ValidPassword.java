package com.example.bankcards.dto.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.bankcards.util.constant.UserConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = UserConstants.Password.DTO_REQUIRED_MESSAGE)
@Size(min = UserConstants.Password.RAW_PASSWORD_MIN_SIZE, message = UserConstants.Password.DTO_INVALID_LENGTH_MESSAGE)
public @interface ValidPassword {
}

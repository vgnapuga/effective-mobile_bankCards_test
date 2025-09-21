package com.example.bankcards.model.card.vo;


import java.math.BigDecimal;

import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseValueObject;


public final class CardBalance extends BaseValueObject<BigDecimal> {

    public CardBalance(final BigDecimal value) {
        super(value);
    }

    @Override
    protected void checkValidation(final BigDecimal value) {
        if (value.scale() > 2)
            throw new DomainValidationException("Card balance cannot have more than 2 decimal places");

        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainValidationException(String.format("Card balance < 0 (%s)", value.toString()));
    }

}

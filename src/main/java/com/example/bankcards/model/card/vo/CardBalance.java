package com.example.bankcards.model.card.vo;


import java.math.BigDecimal;

import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseValueObject;
import com.example.bankcards.util.constant.CardConstants;


public final class CardBalance extends BaseValueObject<BigDecimal> {

    public CardBalance(final BigDecimal value) {
        super(value);
    }

    @Override
    protected void checkValidation(final BigDecimal value) {
        if (value.scale() > 2)
            throw new DomainValidationException(CardConstants.CardBalance.DOMAIN_INVALID_SCALE_MESSAGE);

        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainValidationException(CardConstants.CardBalance.domainNegativeBalanceMessage(value));
    }

}

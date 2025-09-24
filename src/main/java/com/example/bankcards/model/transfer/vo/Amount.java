package com.example.bankcards.model.transfer.vo;


import java.math.BigDecimal;

import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseValueObject;
import com.example.bankcards.util.constant.TransferConstants;


public final class Amount extends BaseValueObject<BigDecimal> {

    public Amount(final BigDecimal value) {
        super(value);
    }

    @Override
    protected final void checkValidation(final BigDecimal value) {
        if (value.scale() > 2)
            throw new DomainValidationException(TransferConstants.Amount.DOMAIN_INVALID_SCALE_MESSAGE);

        if (value.compareTo(TransferConstants.Amount.MIN_VALUE) < 0)
            throw new DomainValidationException(TransferConstants.Amount.domainNegativeOrZeroMessage(value));
    }

}

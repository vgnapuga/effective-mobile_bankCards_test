package com.example.bankcards.model.transfer.converter;


import java.math.BigDecimal;

import com.example.bankcards.model.transfer.vo.Amount;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class AmountConverter implements AttributeConverter<Amount, BigDecimal> {

    @Override
    public final BigDecimal convertToDatabaseColumn(final Amount attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public final Amount convertToEntityAttribute(final BigDecimal dbData) {
        return dbData == null ? null : new Amount(dbData);
    }

}

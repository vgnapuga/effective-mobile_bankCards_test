package com.example.bankcards.model.card.converter;


import java.math.BigDecimal;

import com.example.bankcards.model.card.vo.CardBalance;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class CardBalanceConverter implements AttributeConverter<CardBalance, BigDecimal> {

    @Override
    public final BigDecimal convertToDatabaseColumn(final CardBalance attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public final CardBalance convertToEntityAttribute(final BigDecimal dbData) {
        return dbData == null ? null : new CardBalance(dbData);
    }

}

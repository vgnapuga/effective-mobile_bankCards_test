package com.example.bankcards.model.card.converter;


import java.time.LocalDate;

import com.example.bankcards.model.card.vo.CardExpiryDate;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class CardExpiryDateConverter implements AttributeConverter<CardExpiryDate, LocalDate> {

    @Override
    public final LocalDate convertToDatabaseColumn(final CardExpiryDate attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public final CardExpiryDate convertToEntityAttribute(final LocalDate dbData) {
        return dbData == null ? null : CardExpiryDate.of(dbData.getYear(), dbData.getMonthValue());
    }

}

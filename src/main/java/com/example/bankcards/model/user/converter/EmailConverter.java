package com.example.bankcards.model.user.converter;


import com.example.bankcards.model.user.vo.Email;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public final class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(final Email attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Email convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : new Email(dbData);
    }

}

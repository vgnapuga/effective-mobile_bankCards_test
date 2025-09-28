package com.example.bankcards.model.user.converter;


import com.example.bankcards.model.user.vo.Password;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public final class PasswordConverter implements AttributeConverter<Password, String> {

    @Override
    public String convertToDatabaseColumn(final Password attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Password convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : new Password(dbData);
    }

}

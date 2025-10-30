package com.example.bankcards.model.user.converter;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.bankcards.model.user.Role;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public final class RoleConverter implements AttributeConverter<Set<Role>, String> {

    private static final String DELIMITER = ", ";

    @Override
    public final String convertToDatabaseColumn(final Set<Role> roles) {
        return roles == null ? null : roles.stream().map(Role::name).collect(Collectors.joining(DELIMITER));
    }

    @Override
    public final Set<Role> convertToEntityAttribute(String dbData) {
        return dbData == null ? null
                : Arrays.stream(dbData.split(DELIMITER)).map(String::trim).map(Role::valueOf).collect(
                        Collectors.toSet());
    }

}

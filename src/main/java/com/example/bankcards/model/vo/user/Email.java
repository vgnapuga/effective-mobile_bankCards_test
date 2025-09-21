package com.example.bankcards.model.vo.user;


import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.vo.BaseValueObject;


public final class Email extends BaseValueObject<String> {

    private static final int MAX_LENGTH = 255;
    private static final String REGEX = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    public Email(final String value) {
        super(value);
    }

    @Override
    protected void checkValidation(final String value) {
        if (value.isBlank())
            throw new DomainValidationException("Email value is <blank>");

        if (value.length() > MAX_LENGTH)
            throw new DomainValidationException(
                    String.format("Invalid email length: %d (max allowed: %d)", value.length(), MAX_LENGTH));

        if (!value.matches(REGEX))
            throw new DomainValidationException("Invalid email format");
    }

}

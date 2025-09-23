package com.example.bankcards.model.user.vo;


import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseValueObject;
import com.example.bankcards.util.constant.UserConstants;


public final class Email extends BaseValueObject<String> {

    public Email(final String value) {
        super(value);
    }

    @Override
    protected void checkValidation(final String value) {
        if (value.isBlank())
            throw new DomainValidationException(UserConstants.Email.DOMAIN_BLANK_MESSAGE);

        if (value.length() > UserConstants.Email.MAX_LENGTH)
            throw new DomainValidationException(UserConstants.Email.domainInvalidLengthMessage(value.length()));

        if (!value.matches(UserConstants.Email.REGEX))
            throw new DomainValidationException(UserConstants.Email.DOMAIN_INVALID_FORMAT_MESSAGE);
    }

}

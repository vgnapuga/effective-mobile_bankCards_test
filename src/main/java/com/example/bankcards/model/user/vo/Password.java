package com.example.bankcards.model.user.vo;


import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseValueObject;
import com.example.bankcards.util.constant.UserConstants;


public final class Password extends BaseValueObject<String> {

    public Password(final String hashedValue) {
        super(hashedValue);
    }

    @Override
    protected void checkValidation(final String hashedValue) {
        if (hashedValue.isBlank())
            throw new DomainValidationException(UserConstants.Password.DOMAIN_BLANK_MESSAGE);

        if (hashedValue.length() != UserConstants.Password.BCRYPT_HASH_SIZE)
            throw new DomainValidationException(
                    UserConstants.Password.domainInvalidLengthMessage(hashedValue.length()));

        for (String prefix : UserConstants.Password.BCRYPT_HASH_PREFIXES) {
            if (hashedValue.startsWith(prefix))
                return;
        }
        throw new DomainValidationException(UserConstants.Password.DOMAIN_INVALID_FORMAT_MESSAGE);
    }

    @Override
    public final String toString() {
        return "Password{value=***}";
    }

}

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
            throw new DomainValidationException("Password hashed value is <blank>");

        if (hashedValue.length() != UserConstants.Password.BCRYPT_HASH_SIZE)
            throw new DomainValidationException(
                    UserConstants.Password.domainInvalidLengthMessage(hashedValue.length()));

        for (String prefix : UserConstants.Password.BCRYPT_HASH_PREFIXES) {
            if (hashedValue.startsWith(prefix))
                return;
        }
        throw new DomainValidationException("Invalid password hashed value format");
    }

    @Override
    public final String toString() {
        return "Password{value=***}";
    }

}

package com.example.bankcards.model.vo.user;


import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.vo.BaseValueObject;


public final class Password extends BaseValueObject<String> {

    private static final int BCRYPT_HASH_SIZE = 60;
    private static final String[] BCRYPT_HASH_PREFIXES = { "$2a$", "$2b$", "$2y$" };

    public Password(final String hashedValue) {
        super(hashedValue);
    }

    @Override
    protected void checkValidation(final String hashedValue) {
        if (hashedValue.isBlank())
            throw new DomainValidationException("Password hashed value is <blank>");

        if (hashedValue.length() != BCRYPT_HASH_SIZE)
            throw new DomainValidationException(
                    String.format(
                            "Invalid password hashed value length: %d (should be: %d)",
                            hashedValue.length(),
                            BCRYPT_HASH_SIZE));

        for (String prefix : BCRYPT_HASH_PREFIXES) {
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

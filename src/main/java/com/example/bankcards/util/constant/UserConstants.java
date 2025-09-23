package com.example.bankcards.util.constant;


public final class UserConstants {

    private UserConstants() {
        throw new UnsupportedOperationException("UserConstants.java - utility class");
    }

    public static final class Email {

        public static final int MAX_LENGTH = 255;
        public static final String REGEX = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                "@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

        public static final String DTO_REQUIRED_MESSAGE = "Email is required";
        public static final String DTO_INVALID_FORMAT_MESSAGE = "Invalid email format";
        public static final String DTO_INVALID_LENGTH_MESSAGE = "Email must not exceed " + MAX_LENGTH + " characters";

        public static final String DOMAIN_BLANK_MESSAGE = "Email value is <blank>";
        public static final String DOMAIN_INVALID_FORMAT_MESSAGE = "Invalid email value format";

        private static final String TEMPLATE_DOMAIN_INVALID_LENGTH = "Invalid email value length: %d (max allowed: %d)";

        public static final String domainInvalidLengthMessage(final int actualLength) {
            return String.format(TEMPLATE_DOMAIN_INVALID_LENGTH, actualLength, MAX_LENGTH);
        }

    }

    public static final class Password {

        public static final int BCRYPT_HASH_SIZE = 60;
        public static final int RAW_PASSWORD_MIN_SIZE = 8;
        public static final String[] BCRYPT_HASH_PREFIXES = { "$2a$", "$2b$", "$2y$" };

        public static final String DTO_REQUIRED_MESSAGE = "Password is required";
        public static final String DTO_INVALID_LENGTH_MESSAGE = "Password must be at least " + RAW_PASSWORD_MIN_SIZE +
                " characters";

        public static final String DOMAIN_BLANK_MESSAGE = "Password value is <blank>";
        public static final String DOMAIN_INVALID_FORMAT_MESSAGE = "Invalid password value format";

        private static final String TEMPLATE_DOMAIN_INVALID_LENGTH = "Invalid password value length: %d (must be: %d)";

        public static final String domainInvalidLengthMessage(final int actualLength) {
            return String.format(TEMPLATE_DOMAIN_INVALID_LENGTH, actualLength, BCRYPT_HASH_SIZE);
        }

    }

}

package com.example.bankcards.util.constant;


public class UserConstants {

    public static final String DTO_REQUIRED_MESSAGE = "User is required";

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
        public static final String DOMAIN_INVALID_FORMAT_MESSAGE = "Invalid Email value format";

        private static final String TEMPLATE_DOMAIN_INVALID_LENGTH = "Invalid Email value length: %d (max allowed: %d)";
        private static final String TEMPLATE_ALREADY_EXISTS = "User with email=%s already exists";

        public static final String domainInvalidLengthMessage(final int actualLength) {
            return String.format(TEMPLATE_DOMAIN_INVALID_LENGTH, actualLength, MAX_LENGTH);
        }

        public static final String alreadyExistsMessage(final String email) {
            return String.format(TEMPLATE_ALREADY_EXISTS, email);
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
        public static final String DOMAIN_INVALID_FORMAT_MESSAGE = "Invalid Password value format";

        private static final String TEMPLATE_DOMAIN_INVALID_LENGTH = "Invalid Password value length: %d (must be: %d)";
        private static final String TEMPLATE_SERVICE_INVALID_LENGTH = "Invalid raw password length: %d (must be at least: %d)";

        public static final String domainInvalidLengthMessage(final int actualLength) {
            return String.format(TEMPLATE_DOMAIN_INVALID_LENGTH, actualLength, BCRYPT_HASH_SIZE);
        }

        public static final String servicePasswordInvalidLengthMessage(final int actualLength) {
            return String.format(TEMPLATE_SERVICE_INVALID_LENGTH, actualLength, RAW_PASSWORD_MIN_SIZE);
        }

    }

}

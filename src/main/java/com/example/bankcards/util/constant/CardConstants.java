package com.example.bankcards.util.constant;


import java.math.BigDecimal;


public class CardConstants {

    public static final String DTO_REQUIRED_MESSAGE = "Card is required";

    public static final String DTO_REQUIRED_ID_MESSAGE = "Card ID is required";
    public static final String DTO_NEGATIVE_ID_MESSAGE = "Card ID cannot be negative";

    private CardConstants() {
        throw new UnsupportedOperationException("CardConstants.java - utility class");
    }

    public static final class CardOwner {

        public static final String DTO_REQUIRED_MESSAGE = "Card owner is required";
        public static final String DTO_NEGATIVE_ID_MESSAGE = "Card owner ID cannot be negative";

    }

    public static final class CardNumber {

        public static final int CARD_NUMBER_LENGTH = 16;
        public static final String CARD_REGEX = "\\d{" + CARD_NUMBER_LENGTH + "}";
        public static final int UNMASKED_CARD_NUMBER_LENGTH = 4;

        public static final String DTO_REQUIRED_MESSAGE = "Card number is required";
        public static final String DTO_INVALID_FORMAT_MESSAGE = "Invalid card number format";

        public static final String DOMAIN_BLANK_MESSAGE = "CardNumber value is <blank>";
        public static final String DOMAIN_INVALID_FORMAT_MESSAGE = "Invalid CardNumber format";
        public static final String DOMAIN_LUHN_FAILED_MESSAGE = "CardNumber value failed Luhn checksum validation";

    }

    public static final class CardBalance {

        public static final int SCALE_SIZE = 2;

        public static final String DOMAIN_INVALID_SCALE_MESSAGE = "Card balance cannot have more than " + SCALE_SIZE +
                " decimal places";

        private static final String TEMPLATE_DOMAIN_NEGATIVE_BALANCE = "Card balance cannot be < 0 (actual: %s)";

        public static final String domainNegativeBalanceMessage(final BigDecimal actualBalance) {
            return String.format(TEMPLATE_DOMAIN_NEGATIVE_BALANCE, actualBalance);
        }

    }

    public static final class CardExpiryDate {

        public static final int EXPIRY_DAY_OF_MONTH = 1;

        public static final String DTO_REQUIRED_MESSAGE = "Card expiry date is required";
        public static final String DTO_PAST_DATE_MESSAGE = "Card expiry date cannot be in past for new card";

        private static final String TEMPLATE_DOMAIN_INVALID_FORMAT = "Invalid CardExpiryDate values: year=%d, month=%d";

        public static final String domainInvalidFormatMessage(final int actualYear, final int actualMonth) {
            return String.format(TEMPLATE_DOMAIN_INVALID_FORMAT, actualYear, actualMonth);
        }

    }

}

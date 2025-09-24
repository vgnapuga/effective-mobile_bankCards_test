package com.example.bankcards.util.constant;


import java.math.BigDecimal;


public class TransferConstants {

    public static final class Amount {

        public static final int SCALE_SIZE = 2;
        public static final String MIN_VALUE_STRING = "0.01";
        public static final BigDecimal MIN_VALUE = new BigDecimal(MIN_VALUE_STRING);
        public static final int INTEGER_VALUE = 10;
        public static final int FRACTION_VALUE = 2;

        public static final String DTO_REQUIRED_MESSAGE = "Transfer amount is required";
        public static final String DTO_NEGATIVE_OR_ZERO_MESSAGE = "Transfer amount must be positive";
        public static final String DTO_INVALID_FORMAT_MESSAGE = "Invalid transfer amount format";

        public static final String DOMAIN_INVALID_SCALE_MESSAGE = "Transfer Amount cannot have more than " +
                SCALE_SIZE + " decimal places";

        public static final String BUSINESS_RULE_VIOLATED_MESSAGE = "Transfers can be only between one card owner";

        private static final String TEMPLATE_DOMAIN_NEGATIVE_BALANCE = "Transfer Amount cannot be < %s (actual: %s)";

        public static final String domainNegativeOrZeroMessage(final BigDecimal actualAmount) {
            return String.format(TEMPLATE_DOMAIN_NEGATIVE_BALANCE, MIN_VALUE_STRING, actualAmount);
        }

    }

}

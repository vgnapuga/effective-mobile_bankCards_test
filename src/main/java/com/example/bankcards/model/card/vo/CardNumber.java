package com.example.bankcards.model.card.vo;


import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseValueObject;


public final class CardNumber extends BaseValueObject<String> {

    private static final int CARD_NUMBER_LENGTH = 16;
    private static final String CARD_REGEX = "\\d{" + CARD_NUMBER_LENGTH + "}";
    private static final int UNMASKED_CARD_NUMBER_LENGTH = 4;

    public CardNumber(final String value) {
        super(value);
    }

    @Override
    protected void checkValidation(final String value) {
        if (value.isBlank())
            throw new DomainValidationException("Card number value is <blank>");

        if (value.length() != CARD_NUMBER_LENGTH)
            throw new DomainValidationException(
                    String.format("Invalid card number length: %d (should be %d)", value.length(), CARD_NUMBER_LENGTH));

        if (!value.matches(CARD_REGEX))
            throw new DomainValidationException("Card number must contain only digits");

        if (!passesLuhnCheck(value))
            throw new DomainValidationException("Card number failed Luhn checksum validation");
    }

    private static boolean passesLuhnCheck(final String cardNumber) {
        int sum = 0;
        boolean isEvenPosition = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (isEvenPosition) {
                digit *= 2;

                if (digit > 9)
                    digit = digit - 9;
            }

            sum += digit;
            isEvenPosition = !isEvenPosition;
        }

        return sum % 10 == 0;
    }

    @Override
    public String toString() {
        return String.format(
                "CardNumber{value=**** **** **** %s}",
                value.substring(CARD_NUMBER_LENGTH - UNMASKED_CARD_NUMBER_LENGTH));
    }

}

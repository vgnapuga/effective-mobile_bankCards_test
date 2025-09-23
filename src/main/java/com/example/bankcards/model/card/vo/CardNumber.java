package com.example.bankcards.model.card.vo;


import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseValueObject;
import com.example.bankcards.util.constant.CardConstants;


public final class CardNumber extends BaseValueObject<String> {

    public CardNumber(final String value) {
        super(value);
    }

    @Override
    protected void checkValidation(final String value) {
        if (value.isBlank())
            throw new DomainValidationException(CardConstants.CardNumber.DOMAIN_BLANK_MESSAGE);

        if (!value.matches(CardConstants.CardNumber.CARD_REGEX))
            throw new DomainValidationException(CardConstants.CardNumber.DOMAIN_INVALID_FORMAT_MESSAGE);

        if (!passesLuhnCheck(value))
            throw new DomainValidationException(CardConstants.CardNumber.DOMAIN_LUHN_FAILED_MESSAGE);
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

    public final String getLastDigits() {
        return this.value.substring(
                CardConstants.CardNumber.CARD_NUMBER_LENGTH - CardConstants.CardNumber.UNMASKED_CARD_NUMBER_LENGTH);
    }

    @Override
    public String toString() {
        return String.format("CardNumber{value=**** **** **** %s}", getLastDigits());
    }

}

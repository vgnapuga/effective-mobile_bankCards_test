package com.example.bankcards.model.vo.bankCard;


import java.time.DateTimeException;
import java.time.LocalDate;

import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.vo.BaseValueObject;


public final class CardExpiryDate extends BaseValueObject<LocalDate> {

    public static CardExpiryDate of(final int year, final int month) {
        return new CardExpiryDate(year, month);
    }

    private CardExpiryDate(final int year, final int month) {
        super(createValidLocalDate(year, month));
    }

    private static LocalDate createValidLocalDate(final int year, final int month) {
        try {
            LocalDate validDate = LocalDate.of(year, month, 1);
            return validDate;
        } catch (DateTimeException exception) {
            throw new DomainValidationException(String.format("Invalid expiry date: year=%d, month=%d", year, month));
        }
    }

    @Override
    protected void checkValidation(final LocalDate value) {
        return;
    }

    @Override
    public String toString() {
        return String.format("ExpiryDate{value=%02d/%02d}", this.value.getMonthValue(), this.value.getYear() % 100);
    }

}

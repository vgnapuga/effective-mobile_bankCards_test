package com.example.bankcards.model.vo;


import com.example.bankcards.exception.DomainValidationException;


public abstract class BaseValueObject<T> {

    protected final T value;

    private final String NULL_MESSAGE = this.getClass().getSimpleName() + " value is <null>";

    public BaseValueObject(final T value) {
        validateNotNull(value);
        checkValidation(value);
        this.value = value;
    }

    private final void validateNotNull(final T value) {
        if (value == null)
            throw new DomainValidationException(NULL_MESSAGE);
    }

    protected abstract void checkValidation(final T value);

    public final T getValue() {
        return this.value;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        return java.util.Objects.equals(this.value, ((BaseValueObject<?>) obj).value);
    }

    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return String.format("%s{value=%s}", this.getClass().getSimpleName(), this.getValue().toString());
    }

}

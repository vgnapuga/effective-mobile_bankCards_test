package com.example.bankcards.exception;


public final class DomainValidationException extends RuntimeException {

    public DomainValidationException(final String message) {
        super(message);
    }

}

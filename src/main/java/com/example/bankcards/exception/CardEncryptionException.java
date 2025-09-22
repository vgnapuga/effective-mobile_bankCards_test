package com.example.bankcards.exception;


public final class CardEncryptionException extends RuntimeException {

    public CardEncryptionException(final String message) {
        super(message);
    }

    public CardEncryptionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

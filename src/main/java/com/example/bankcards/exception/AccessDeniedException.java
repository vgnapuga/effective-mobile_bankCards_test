package com.example.bankcards.exception;


public final class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(final String message) {
        super(message);
    }

}

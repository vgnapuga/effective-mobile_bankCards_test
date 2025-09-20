package com.example.bankcards.exception;


public final class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }

}

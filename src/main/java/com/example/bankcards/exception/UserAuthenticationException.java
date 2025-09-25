package com.example.bankcards.exception;


public class UserAuthenticationException extends RuntimeException {

    public UserAuthenticationException(final String message) {
        super(message);
    }

    public static final class NotAuthenticated extends UserAuthenticationException {

        public NotAuthenticated() {
            super("User authentication is required for this operation");
        }

    }

}

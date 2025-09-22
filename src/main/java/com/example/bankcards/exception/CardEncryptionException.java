package com.example.bankcards.exception;


public class CardEncryptionException extends RuntimeException {

    public CardEncryptionException(final String message) {
        super(message);
    }

    public CardEncryptionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static class InvalidKeyException extends CardEncryptionException {

        public InvalidKeyException(final String message) {
            super(message);
        }

    }

    public static class EncryptionFailedException extends CardEncryptionException {

        public EncryptionFailedException(final String message) {
            super(message);
        }

        public EncryptionFailedException(final String message, final Throwable cause) {
            super(message, cause);
        }

    }

    public static class DecryptionFailedException extends CardEncryptionException {

        public DecryptionFailedException(final String message) {
            super(message);
        }

        public DecryptionFailedException(final String message, final Throwable cause) {
            super(message, cause);
        }

    }

    public static class InvalidFormatException extends CardEncryptionException {

        public InvalidFormatException(final String message) {
            super(message);
        }

    }

}

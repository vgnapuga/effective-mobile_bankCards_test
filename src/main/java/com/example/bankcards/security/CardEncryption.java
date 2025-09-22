package com.example.bankcards.security;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.bankcards.exception.CardEncryptionException;
import com.example.bankcards.model.card.vo.CardNumber;


public final class CardEncryption {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final String secretKey;

    public static CardEncryption of(final String secretKey) {
        validateSecretKey(secretKey);
        return new CardEncryption(secretKey);
    }

    private CardEncryption(final String secretKey) {
        this.secretKey = secretKey;
    }

    private static void validateSecretKey(final String secretKey) {
        if (secretKey == null)
            throw new CardEncryptionException.InvalidKeyException("Card encryption secret key is <null>");

        if (secretKey.trim().isBlank())
            throw new CardEncryptionException.InvalidKeyException("Card encryption secret key is <blank>");

        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);

            if (keyBytes.length != 32)
                throw new CardEncryptionException.InvalidKeyException(
                        String.format(
                                "Card encryption secret key must 256 bits (32 bytes), got: %d bytes",
                                keyBytes.length));
        } catch (IllegalArgumentException e) {
            throw new CardEncryptionException.InvalidKeyException("Card encryption secret key is not <Base64>");
        }
    }

    public String encrypt(final CardNumber cardNumber) {
        if (cardNumber == null)
            throw new CardEncryptionException.InvalidFormatException("Card number for encrypt is <null>");

        try {
            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), KEY_ALGORITHM);

            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] encryptedData = cipher.doFinal(cardNumber.getValue().getBytes(StandardCharsets.UTF_8));

            byte[] result = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, result, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, result, GCM_IV_LENGTH, encryptedData.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (CardEncryptionException e) {
            throw e;
        } catch (Exception e) {
            throw new CardEncryptionException.EncryptionFailedException("Card encryption failed: " + e.getMessage(), e);
        }
    }

    public CardNumber decrypt(final String encryptedCardNumber) {
        if (encryptedCardNumber == null)
            throw new CardEncryptionException.InvalidFormatException("Card number for decrypt is <null>");

        if (encryptedCardNumber.isBlank())
            throw new CardEncryptionException.InvalidFormatException("Card number for decrypt is <blank>");

        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedCardNumber);

            if (encryptedBytes.length < GCM_IV_LENGTH)
                throw new CardEncryptionException.InvalidFormatException("Encrypted data too short");

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[encryptedBytes.length - GCM_IV_LENGTH];

            System.arraycopy(encryptedBytes, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedBytes, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), KEY_ALGORITHM);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] decryptedData = cipher.doFinal(cipherText);
            String result = new String(decryptedData, StandardCharsets.UTF_8);

            return new CardNumber(result);
        } catch (CardEncryptionException e) {
            throw e;
        } catch (Exception e) {
            throw new CardEncryptionException.DecryptionFailedException("Card decryption failed: " + e.getMessage(), e);
        }
    }

}

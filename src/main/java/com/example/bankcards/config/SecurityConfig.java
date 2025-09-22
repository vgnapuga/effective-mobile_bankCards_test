package com.example.bankcards.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.bankcards.security.CardEncryption;


@Configuration
public class SecurityConfig {

    @Bean
    public CardEncryption cardEncryption(@Value("${app.security.card-encryption-key}") final String secretKey) {
        return CardEncryption.of(secretKey);
    }

}

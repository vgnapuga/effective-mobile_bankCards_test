package com.example.bankcards.config;


import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.bankcards.config.properties.CorsProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private static final String API_PATTERN = "/api/**";

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Configuring CORS for pattern: {}", API_PATTERN);

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(corsProperties.origins()));
        configuration.setAllowedMethods(Arrays.asList(corsProperties.methods()));
        configuration.setAllowedHeaders(Arrays.asList(corsProperties.headers()));
        configuration.setAllowCredentials(corsProperties.allowedCredentials());
        configuration.setMaxAge(corsProperties.cache().maxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(API_PATTERN, configuration);

        log.info("CORS configured successfully. Allowed origins: {}", Arrays.toString(corsProperties.origins()));
        return source;
    }

}

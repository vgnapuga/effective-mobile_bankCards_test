package com.example.bankcards.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app.front")
public record CorsProperties(
        String[] origins,
        String[] methods,
        String[] headers,
        boolean allowedCredentials,
        Cache cache) {

    public record Cache(long maxAge) {
    }

}

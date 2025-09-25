package com.example.bankcards.security;


import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public final class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(final UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        List<String> roles = customUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList());

        return Jwts.builder().setSubject(customUserDetails.getUsername()).claim("userId", customUserDetails.getUserId())
                .claim("roles", roles).setIssuedAt(new Date()).setExpiration(
                        new Date(System.currentTimeMillis() + jwtExpirationMs)).signWith(
                                getSigningKey(),
                                SignatureAlgorithm.HS256).compact();
    }

    public boolean validateToken(final String token) {
        try {
            generateClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    private Claims generateClaims(final String token) {
        return Jwts.parserBuilder().setSigningKey(this.getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public String getEmailFromToken(final String token) {
        try {
            return this.generateClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from token: {}", e.getMessage());
            return null;
        }

    }

    public Long getUserIdFromToken(String token) {
        try {
            return this.generateClaims(token).get("userId", Long.class);
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

}

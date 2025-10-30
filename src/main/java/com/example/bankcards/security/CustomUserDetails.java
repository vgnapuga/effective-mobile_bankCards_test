package com.example.bankcards.security;


import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.bankcards.model.user.User;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final GrantedAuthority authority;

    public static CustomUserDetails of(User user) {
        log.debug("Creating CustomUserDetails for user: {}", user.getId());

        GrantedAuthority authoritiy = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());

        log.debug("Created 1 authority");

        return new CustomUserDetails(
                user.getId(),
                user.getEmail().getValue(),
                user.getPassword().getValue(),
                authoritiy);
    }

    private CustomUserDetails(
            final Long userId,
            final String email,
            final String password,
            final GrantedAuthority authority) {
        log.debug(
                "CustomUserDetails constructor called with userId: {}, email: {}, authorities: {}",
                userId,
                email,
                authority);
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

    public Long getUserId() {
        return this.userId;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(this.authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

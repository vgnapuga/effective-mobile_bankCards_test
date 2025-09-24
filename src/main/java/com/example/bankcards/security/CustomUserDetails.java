package com.example.bankcards.security;


import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.bankcards.model.user.User;


public final class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final Set<GrantedAuthority> authorities;

    public static CustomUserDetails of(User user) {
        Set<GrantedAuthority> authorities = user.getRoles().stream().map(
                role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toString())).collect(Collectors.toSet());

        return new CustomUserDetails(
                user.getId(),
                user.getEmail().getValue(),
                user.getPassword().getValue(),
                authorities);
    }

    private CustomUserDetails(
            final Long userId,
            final String email,
            final String password,
            final Set<GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
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
        return this.authorities;
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

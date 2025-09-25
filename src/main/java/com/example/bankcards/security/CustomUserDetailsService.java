package com.example.bankcards.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.user.User;
import com.example.bankcards.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public final class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        try {
            User user = userService.findUserByEmail(email);
            return CustomUserDetails.of(user);
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException(String.format("User with email=%s not found", email));
        }
    }

}

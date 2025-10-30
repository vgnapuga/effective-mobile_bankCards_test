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
            log.debug("Found user: {}", user.getEmail());

            CustomUserDetails userDetails = CustomUserDetails.of(user);
            log.debug("Created UserDetails with {} authorities", userDetails.getAuthorities().size());

            return userDetails;
        } catch (ResourceNotFoundException e) {
            log.error("User not found: {}", email, e);
            throw new UsernameNotFoundException(String.format("User with email=%s not found", email));
        } catch (Exception e) {
            log.error("Error loading user: {}", email, e);
            throw new UsernameNotFoundException("Error loading user", e);
        }
    }

}

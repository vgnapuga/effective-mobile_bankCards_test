package com.example.bankcards.controller;


import org.springframework.security.core.Authentication;

import com.example.bankcards.exception.UserAuthenticationException;
import com.example.bankcards.security.CustomUserDetails;


public abstract class BaseController {

    protected Long getCurrentUserId(final Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null)
            throw new UserAuthenticationException.NotAuthenticated();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

}

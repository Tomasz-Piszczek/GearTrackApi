package com.example.geartrackapi.security;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {
    
    private SecurityUtils() {
    }

    public static UUID authenticatedUserId() {
        return getCurrentUserId().orElseThrow(() -> new EntityNotFoundException("User Id not found"));
    }

    public static Optional<UUID> getCurrentUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipalUserId(securityContext.getAuthentication()));
    }

    public static UUID extractPrincipalUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser springSecurityUser) {
            return springSecurityUser.getUserId();
        }
        return null;
    }

}
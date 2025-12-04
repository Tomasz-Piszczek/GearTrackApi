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
        return getCurrentUserId();
    }

    public static UUID getCurrentUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return extractPrincipalUserId(securityContext.getAuthentication());
    }

    public static UUID getCurrentOrganizationId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return extractPrincipalOrganizationId(securityContext.getAuthentication());
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

    public static UUID extractPrincipalOrganizationId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser springSecurityUser) {
            return springSecurityUser.getOrganizationId();
        }
        return null;
    }

}
package com.flashlearn.app.security;

import com.flashlearn.app.model.dto.AuthUserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthUserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUserDto user)) {
            return null;
        }
        return user;
    }

    public static AuthUserDto requireCurrentUser() {
        AuthUserDto user = getCurrentUser();
        if (user == null) {
            throw new com.flashlearn.app.exception.AppException(401, "Authentication required");
        }
        return user;
    }
}

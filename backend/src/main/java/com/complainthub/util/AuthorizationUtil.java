package com.complainthub.util;

import com.complainthub.entity.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class AuthorizationUtil {
    private AuthorizationUtil() {
    }

    public static boolean hasRole(HttpServletRequest request, UserRole requiredRole) {
        if (requiredRole == null) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object sessionRole = session.getAttribute(AuthenticationConstants.USER_ROLE);
        if (sessionRole == null) {
            return false;
        }
        return requiredRole.name().equals(sessionRole.toString());
    }

    public static boolean isAdmin(HttpServletRequest request) {
        return hasRole(request, UserRole.ADMIN);
    }

    public static boolean isUser(HttpServletRequest request, long userId) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object sessionUserId = session.getAttribute(AuthenticationConstants.USER_ID);
        if (sessionUserId == null) {
            return false;
        }

        try {
            long authenticatedUserId = Long.parseLong(sessionUserId.toString());
            return authenticatedUserId == userId;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static void requireRole(HttpServletRequest request, UserRole requiredRole) {
        if (!hasRole(request, requiredRole)) {
            throw new AuthorizationException("You do not have permission to perform this action.");
        }
    }

    public static void requireOwnerOrAdmin(HttpServletRequest request, long ownerId) {
        if (!isAdmin(request) && !isUser(request, ownerId)) {
            throw new AuthorizationException("You do not have permission to access this complaint.");
        }
    }
}
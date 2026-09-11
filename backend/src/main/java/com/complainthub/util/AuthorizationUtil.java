package com.complainthub.util;

import com.complainthub.entity.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class AuthorizationUtil {
    private AuthorizationUtil(){

    }

    public static boolean hasRole(HttpServletRequest req, UserRole requiredRole){
        HttpSession session = req.getSession(false);
        if(session == null){
            return false;
        }

        Object sessionRole = session.getAttribute(AuthenticationConstants.USER_ROLE);
        if(sessionRole == null){
            return false;
        }
        return requiredRole.name().equals(sessionRole.toString());
    }

    public static void requireRole(HttpServletRequest req, UserRole requireRole){
        if(!hasRole(req, requireRole)){
            throw new AuthorizationException("You do not have permission to perform this action.");
        }
    }
}

package com.inclusivefinance.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public final class SecurityUtils {

    private static final String USER_ATTR = "jwt_claims";

    private SecurityUtils() {}

    public static void setClaims(HttpServletRequest request, Claims claims) {
        request.setAttribute(USER_ATTR, claims);
    }

    public static Claims getClaims(HttpServletRequest request) {
        return (Claims) request.getAttribute(USER_ATTR);
    }

    public static Long getUserId(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) return null;
        return claims.get("userId", Long.class);
    }

    public static String getUsername(HttpServletRequest request) {
        Claims claims = getClaims(request);
        return claims != null ? claims.getSubject() : null;
    }

    public static String getRole(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) return null;
        return claims.get("role", String.class);
    }

    public static Long getEnterpriseId(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) return null;
        return claims.get("enterpriseId", Long.class);
    }
}

package com.hackaton.module.common.security;

import com.hackaton.module.common.exception.ForbiddenException;

public record Caller(String email, String role) {

    public static final String ADMIN = "ADMIN";
    static final String UNKNOWN_AUTHOR = "inconnu";
    private static final String ADMIN_ONLY = "Action réservée au gérant (rôle ADMIN)";

    public boolean isAdmin() {
        return ADMIN.equals(role);
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new ForbiddenException(ADMIN_ONLY);
        }
    }

    public String author() {
        return email == null || email.isBlank() ? UNKNOWN_AUTHOR : email.trim();
    }
}

package com.hackaton.rh.common.security;

public record Actor(Long id, String role) {

    private static final String ADMIN = "ADMIN";

    public boolean isAdmin() {
        return ADMIN.equals(role);
    }
}

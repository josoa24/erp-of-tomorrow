package com.hackaton.stock.common.security;

/** Utilisateur à l'origine d'une requête, transmis par la gateway (en-têtes X-User-*). */
public record Actor(Long id, String email, String role) {

    public static final String ADMIN = "ADMIN";

    public boolean isAdmin() {
        return ADMIN.equals(role);
    }

    public boolean isSameUser(Long otherId) {
        return id != null && id.equals(otherId);
    }
}

package com.hackaton.gateway.security;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RoleAccessPolicy {

    private static final String ADMIN = "ADMIN";
    private static final String LOGIN_PATH = "/api/auth/login";

    private static final Map<String, Set<String>> MODULE_ROLES = Map.of(
            "/api/stock", Set.of("STOCK"),
            "/api/vente", Set.of("VENTE"),
            "/api/rh", Set.of("RH"),
            "/api/crm", Set.of("CRM"),
            "/api/auth", Set.of());

    public boolean isPublic(String path) {
        return LOGIN_PATH.equals(path);
    }

    public boolean isAllowed(String path, String role) {
        if (ADMIN.equals(role)) {
            return true;
        }
        return MODULE_ROLES.entrySet().stream()
                .anyMatch(rule -> matches(path, rule.getKey()) && rule.getValue().contains(role));
    }

    private boolean matches(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}

package com.hackaton.gateway.security;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class RoleAccessPolicy {

    private static final String ADMIN = "ADMIN";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String AI_TEST_PAGE = "/api/ai/ui";
    private static final String OWN_ACCOUNT_PATH = "/api/auth/users/me";
    private static final Pattern API_DOCS_PATH = Pattern.compile("^/api/[a-z]+/v3/api-docs(/.*)?$");

    private static final Map<String, Set<String>> MODULE_ROLES = Map.of(
            "/api/stock", Set.of("STOCK"),
            "/api/vente", Set.of("VENTE"),
            "/api/rh", Set.of("RH"),
            "/api/crm", Set.of("CRM"),
            "/api/ai", Set.of("STOCK", "VENTE", "RH", "CRM"),
            "/api/voice", Set.of("STOCK", "VENTE", "RH", "CRM"),
            "/api/modules", Set.of("STOCK", "VENTE", "RH", "CRM"),
            "/api/auth", Set.of());

    public boolean isPublic(String path) {
        return LOGIN_PATH.equals(path) || AI_TEST_PAGE.equals(path) || API_DOCS_PATH.matcher(path).matches();
    }

    public boolean isAllowed(String path, String role) {
        if (ADMIN.equals(role) || matches(path, OWN_ACCOUNT_PATH)) {
            return true;
        }
        return MODULE_ROLES.entrySet().stream()
                .anyMatch(rule -> matches(path, rule.getKey()) && rule.getValue().contains(role));
    }

    private boolean matches(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}

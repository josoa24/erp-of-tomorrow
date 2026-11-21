package com.hackaton.gateway.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApiDocsAccessTest {

    private final RoleAccessPolicy policy = new RoleAccessPolicy();

    @Test
    void shouldExposeEachServiceApiDocsWithoutToken() {
        assertTrue(policy.isPublic("/api/stock/v3/api-docs"));
        assertTrue(policy.isPublic("/api/vente/v3/api-docs"));
        assertTrue(policy.isPublic("/api/rh/v3/api-docs"));
        assertTrue(policy.isPublic("/api/crm/v3/api-docs"));
        assertTrue(policy.isPublic("/api/auth/v3/api-docs"));
        assertTrue(policy.isPublic("/api/stock/v3/api-docs/swagger-config"));
    }

    @Test
    void shouldKeepBusinessEndpointsProtected() {
        assertFalse(policy.isPublic("/api/stock/products"));
        assertFalse(policy.isPublic("/api/auth/users"));
        assertFalse(policy.isPublic("/api/stock/products/v3/api-docs-x"));
        assertFalse(policy.isPublic("/api/stock/products/1/v3/api-docs"));
    }

    @Test
    void shouldKeepLoginPublic() {
        assertTrue(policy.isPublic("/api/auth/login"));
    }
}

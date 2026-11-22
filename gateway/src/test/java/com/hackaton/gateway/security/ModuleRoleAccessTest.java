package com.hackaton.gateway.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModuleRoleAccessTest {

    private final RoleAccessPolicy policy = new RoleAccessPolicy();

    @Test
    void shouldOpenModulesToEveryBusinessRole() {
        for (String role : new String[] {"STOCK", "VENTE", "RH", "CRM", "ADMIN"}) {
            assertTrue(policy.isAllowed("/api/modules/definitions", role), role);
            assertTrue(policy.isAllowed("/api/modules/definitions/livraisons/records", role), role);
            assertTrue(policy.isAllowed("/api/modules/adaptations", role), role);
        }
    }

    @Test
    void shouldRefuseUnknownRole_onModules() {
        assertFalse(policy.isAllowed("/api/modules/definitions", "INCONNU"));
    }

    @Test
    void shouldNotMatchLookalikePrefix() {
        assertFalse(policy.isAllowed("/api/modulesx/definitions", "STOCK"));
        assertFalse(policy.isAllowed("/api/module/definitions", "VENTE"));
    }

    @Test
    void shouldExposeModuleApiDocsWithoutToken_butProtectData() {
        assertTrue(policy.isPublic("/api/modules/v3/api-docs"));
        assertFalse(policy.isPublic("/api/modules/definitions"));
    }
}

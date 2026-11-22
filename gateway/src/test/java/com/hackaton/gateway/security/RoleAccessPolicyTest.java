package com.hackaton.gateway.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RoleAccessPolicyTest {

    private final RoleAccessPolicy policy = new RoleAccessPolicy();

    @Test
    void shouldAllowRhOnlyOnRhModule() {
        assertTrue(policy.isAllowed("/api/rh/employees", "RH"));
        assertFalse(policy.isAllowed("/api/vente/sales", "RH"));
        assertFalse(policy.isAllowed("/api/stock/products", "RH"));
    }

    @Test
    void shouldAllowVenteOnlyOnVenteModule() {
        assertTrue(policy.isAllowed("/api/vente/sales", "VENTE"));
        assertFalse(policy.isAllowed("/api/rh/employees", "VENTE"));
        assertFalse(policy.isAllowed("/api/stock/products", "VENTE"));
    }

    @Test
    void shouldAllowStockOnlyOnStockModule() {
        assertTrue(policy.isAllowed("/api/stock/products", "STOCK"));
        assertFalse(policy.isAllowed("/api/vente/sales", "STOCK"));
        assertFalse(policy.isAllowed("/api/rh/employees", "STOCK"));
    }

    @Test
    void shouldAllowAdminEverywhere() {
        assertTrue(policy.isAllowed("/api/stock/products", "ADMIN"));
        assertTrue(policy.isAllowed("/api/vente/sales", "ADMIN"));
        assertTrue(policy.isAllowed("/api/rh/employees", "ADMIN"));
        assertTrue(policy.isAllowed("/api/auth/users", "ADMIN"));
    }

    @Test
    void shouldAllowVoiceOnlyForRhAndAdmin() {
        assertTrue(policy.isAllowed("/api/voice/transcribe", "RH"));
        assertTrue(policy.isAllowed("/api/voice/transcribe", "ADMIN"));
        assertFalse(policy.isAllowed("/api/voice/transcribe", "VENTE"));
        assertFalse(policy.isAllowed("/api/voice/transcribe", "STOCK"));
    }

    @Test
    void shouldReserveUserManagementToAdmin() {
        assertFalse(policy.isAllowed("/api/auth/users", "RH"));
        assertFalse(policy.isAllowed("/api/auth/users", "STOCK"));
    }

    @Test
    void shouldDenyUnknownPathsAndLookalikePrefixes() {
        assertFalse(policy.isAllowed("/api/inconnu/x", "RH"));
        assertFalse(policy.isAllowed("/api/rhx/employees", "RH"));
    }

    @Test
    void shouldExposeOnlyLoginAsPublic() {
        assertTrue(policy.isPublic("/api/auth/login"));
        assertFalse(policy.isPublic("/api/auth/users"));
    }

    @Test
    void shouldOpenAssistantToEveryRole() {
        assertTrue(policy.isAllowed("/api/ai/agent/chat", "STOCK"));
        assertTrue(policy.isAllowed("/api/ai/agent/chat/stream", "VENTE"));
        assertTrue(policy.isAllowed("/api/ai/agent/chat", "RH"));
        assertTrue(policy.isAllowed("/api/ai/agent/chat", "CRM"));
        assertTrue(policy.isAllowed("/api/ai/agent/chat", "ADMIN"));
        assertFalse(policy.isAllowed("/api/aix/agent/chat", "STOCK"));
    }

    @Test
    void shouldExposeOnlyAssistantTestPageAsPublic() {
        assertTrue(policy.isPublic("/api/ai/ui"));
        assertTrue(policy.isPublic("/api/ai/v3/api-docs"));
        assertFalse(policy.isPublic("/api/ai/agent/chat"));
        assertFalse(policy.isPublic("/api/ai/tools"));
    }
}
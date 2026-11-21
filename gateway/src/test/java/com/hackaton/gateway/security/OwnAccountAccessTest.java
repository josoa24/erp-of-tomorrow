package com.hackaton.gateway.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OwnAccountAccessTest {

    private final RoleAccessPolicy policy = new RoleAccessPolicy();

    @Test
    void shouldLetEveryRoleChangeItsOwnPassword() {
        for (String role : new String[] {"STOCK", "VENTE", "RH", "CRM", "ADMIN"}) {
            assertTrue(policy.isAllowed("/api/auth/users/me/password", role), role);
        }
    }

    @Test
    void shouldKeepAccountManagementForAdmin() {
        assertFalse(policy.isAllowed("/api/auth/users", "STOCK"));
        assertFalse(policy.isAllowed("/api/auth/users/2/disable", "VENTE"));
        assertFalse(policy.isAllowed("/api/auth/users/meta", "RH"));
    }

    @Test
    void shouldNotBePublic() {
        assertFalse(policy.isPublic("/api/auth/users/me/password"));
    }
}

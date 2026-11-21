package com.hackaton.gateway.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CrmRoleAccessTest {

    private final RoleAccessPolicy policy = new RoleAccessPolicy();

    @Test
    void shouldAllowCrmRoleOnlyOnCrmModule() {
        assertTrue(policy.isAllowed("/api/crm/parties/CUSTOMER/1/interactions", "CRM"));
        assertFalse(policy.isAllowed("/api/vente/sales", "CRM"));
        assertFalse(policy.isAllowed("/api/stock/products", "CRM"));
        assertFalse(policy.isAllowed("/api/rh/employees", "CRM"));
        assertFalse(policy.isAllowed("/api/auth/users", "CRM"));
    }

    @Test
    void shouldKeepOtherRolesOutOfCrmModule() {
        assertFalse(policy.isAllowed("/api/crm/parties/CUSTOMER/1/timeline", "VENTE"));
        assertFalse(policy.isAllowed("/api/crm/parties/SUPPLIER/1/timeline", "STOCK"));
        assertFalse(policy.isAllowed("/api/crm/parties/CUSTOMER/1/timeline", "RH"));
    }

    @Test
    void shouldAllowAdminOnCrmModule() {
        assertTrue(policy.isAllowed("/api/crm/parties/CUSTOMER/1/interactions", "ADMIN"));
    }

    @Test
    void shouldNotMatchLookalikePrefix() {
        assertFalse(policy.isAllowed("/api/crmx/anything", "CRM"));
    }
}

package com.hackaton.module.common.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.common.exception.ForbiddenException;
import org.junit.jupiter.api.Test;

class CallerTest {

    @Test
    void shouldAcceptAdmin_whenRoleIsAdmin() {
        Caller caller = new Caller("hery.rabe@nova.mg", "ADMIN");

        assertTrue(caller.isAdmin());
        assertDoesNotThrow(caller::requireAdmin);
    }

    @Test
    void shouldRefuseAdminAction_whenRoleIsBusiness() {
        Caller caller = new Caller("rasoa.andria@nova.mg", "VENTE");

        assertFalse(caller.isAdmin());
        assertThrows(ForbiddenException.class, caller::requireAdmin);
    }

    @Test
    void shouldRefuseAdminAction_whenRoleIsMissing() {
        assertThrows(ForbiddenException.class, () -> new Caller(null, null).requireAdmin());
    }

    @Test
    void shouldUseTrimmedEmailAsAuthor_whenEmailIsPresent() {
        assertEquals("toky.rakoto@nova.mg", new Caller(" toky.rakoto@nova.mg ", "RH").author());
    }

    @Test
    void shouldUseUnknownAuthor_whenEmailIsBlank() {
        assertEquals(Caller.UNKNOWN_AUTHOR, new Caller("  ", "RH").author());
        assertEquals(Caller.UNKNOWN_AUTHOR, new Caller(null, "RH").author());
    }
}

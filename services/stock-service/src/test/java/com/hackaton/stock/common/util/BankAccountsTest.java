package com.hackaton.stock.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BankAccountsTest {

    @Test
    void shouldCompareAccountsIgnoringSpacesDashesAndCase() {
        assertTrue(BankAccounts.sameAccount("MG46 0000-5000 1234", "mg46000050001234"));
        assertFalse(BankAccounts.sameAccount("MG46 0000 5000 1234", "MG46 0000 5000 9999"));
    }

    @Test
    void shouldExposeOnlyLastFourCharacters() {
        assertEquals("1234", BankAccounts.last4("MG46 0000 5000 1234"));
        assertEquals("12", BankAccounts.last4("12"));
        assertNull(BankAccounts.last4("  "));
    }

    @Test
    void shouldTreatBlankAsMissing() {
        assertNull(BankAccounts.clean(" "));
        assertEquals("MG46 1", BankAccounts.clean(" MG46 1 "));
    }
}

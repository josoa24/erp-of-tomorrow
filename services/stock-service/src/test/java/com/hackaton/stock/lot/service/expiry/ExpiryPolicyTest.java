package com.hackaton.stock.lot.service.expiry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.lot.config.ExpiryProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class ExpiryPolicyTest {

    private final ExpiryPolicy policy = new ExpiryPolicy(
            Clock.fixed(Instant.parse("2026-11-21T22:30:00Z"), ZoneId.of("Indian/Antananarivo")),
            new ExpiryProperties(30));

    @Test
    void shouldCountDaysFromTodayInAntananarivo() {
        assertEquals(23, policy.daysToExpiry(LocalDate.of(2026, 12, 15)));
    }

    @Test
    void shouldReturnNegativeDays_whenLotIsAlreadyExpired() {
        assertEquals(-2, policy.daysToExpiry(LocalDate.of(2026, 11, 20)));
    }

    @Test
    void shouldReturnNull_whenThereIsNoExpiryDate() {
        assertNull(policy.daysToExpiry(null));
    }

    @Test
    void shouldFlagExpiringSoon_whenWithinAlertDaysOrExpired() {
        assertTrue(policy.isExpiringSoon(30));
        assertTrue(policy.isExpiringSoon(-2));
        assertFalse(policy.isExpiringSoon(31));
        assertFalse(policy.isExpiringSoon(null));
    }
}

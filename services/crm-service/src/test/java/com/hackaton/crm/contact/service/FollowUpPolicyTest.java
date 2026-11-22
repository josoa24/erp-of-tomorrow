package com.hackaton.crm.contact.service;

import static com.hackaton.crm.contact.ContactFixtures.CLOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.crm.contact.dto.ContactStatus;
import com.hackaton.crm.contact.service.policy.FollowUpPolicy;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class FollowUpPolicyTest {

    private static final Instant LONG_AGO = Instant.parse("2025-01-01T08:00:00Z");

    private final FollowUpPolicy policy = new FollowUpPolicy(CLOCK, 30);

    private ContactStatus statusAfterExchangeAt(String occurredAt) {
        return policy.statusOf(Instant.parse(occurredAt), LONG_AGO);
    }

    @Test
    void shouldBeActive_whenLastExchangeIs29DaysOld() {
        assertEquals(ContactStatus.ACTIVE, statusAfterExchangeAt("2026-10-24T07:00:00Z"));
    }

    @Test
    void shouldBeActive_whenLastExchangeIsExactly30DaysOld() {
        assertEquals(ContactStatus.ACTIVE, statusAfterExchangeAt("2026-10-23T07:00:00Z"));
    }

    @Test
    void shouldBeFollowUp_whenLastExchangeIs31DaysOld() {
        assertEquals(ContactStatus.FOLLOW_UP, statusAfterExchangeAt("2026-10-22T07:00:00Z"));
    }

    @Test
    void shouldCountDaysInAntananarivo_whenExchangeHappenedLateInTheUtcEvening() {
        assertEquals(ContactStatus.ACTIVE, statusAfterExchangeAt("2026-10-22T22:30:00Z"));
    }

    @Test
    void shouldUseCreationDate_whenContactHasNoExchange() {
        assertEquals(ContactStatus.ACTIVE, policy.statusOf(null, Instant.parse("2026-11-01T08:00:00Z")));
    }

    @Test
    void shouldBeFollowUp_whenContactWithoutExchangeIsOlderThan30Days() {
        assertEquals(ContactStatus.FOLLOW_UP, policy.statusOf(null, Instant.parse("2026-09-01T08:00:00Z")));
    }

    @Test
    void shouldKeepTheMostRecentDate_whenCreationIsNewerThanLastExchange() {
        assertEquals(ContactStatus.ACTIVE,
                policy.statusOf(Instant.parse("2026-08-01T08:00:00Z"), Instant.parse("2026-11-10T08:00:00Z")));
    }

    @Test
    void shouldBeFollowUp_whenNoDateIsKnown() {
        assertEquals(ContactStatus.FOLLOW_UP, policy.statusOf(null, null));
    }
}

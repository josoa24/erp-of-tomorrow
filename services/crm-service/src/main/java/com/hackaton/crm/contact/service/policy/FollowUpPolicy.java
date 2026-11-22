package com.hackaton.crm.contact.service.policy;

import com.hackaton.crm.contact.dto.ContactStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FollowUpPolicy {

    private final Clock clock;
    private final int followUpDays;

    public FollowUpPolicy(Clock clock, @Value("${crm.follow-up-days}") int followUpDays) {
        this.clock = clock;
        this.followUpDays = followUpDays;
    }

    public ContactStatus statusOf(Instant lastInteractionAt, Instant createdAt) {
        Instant reference = latest(lastInteractionAt, createdAt);
        if (reference == null) {
            return ContactStatus.FOLLOW_UP;
        }
        LocalDate referenceDay = LocalDate.ofInstant(reference, clock.getZone());
        long silentDays = ChronoUnit.DAYS.between(referenceDay, LocalDate.now(clock));
        return silentDays > followUpDays ? ContactStatus.FOLLOW_UP : ContactStatus.ACTIVE;
    }

    private static Instant latest(Instant first, Instant second) {
        if (first == null) {
            return second;
        }
        return second == null || first.isAfter(second) ? first : second;
    }
}

package com.hackaton.crm.timeline.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TimelineEntry(
        TimelineKind kind, Instant date, String title, String detail, BigDecimal amount, Long reference) {
}

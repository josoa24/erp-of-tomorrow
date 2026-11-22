package com.hackaton.module.support;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public final class TestClocks {

    public static final ZoneId ANTANANARIVO = ZoneId.of("Indian/Antananarivo");
    public static final Instant NOW = Instant.parse("2026-11-22T09:00:00Z");
    public static final Clock SUNDAY_NOON = Clock.fixed(NOW, ANTANANARIVO);

    private TestClocks() {
    }
}

package com.hackaton.module.insight.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.TestClocks;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TodayScopeTest {

    private final TodayScope scope = new TodayScope(TestClocks.SUNDAY_NOON);

    @Test
    void shouldUseCreationDayInAntananarivo_forTimeColumns() {
        ModuleColumn heure = column(ColumnType.TIME);

        assertTrue(scope.isToday(record(Map.of("jour", "08:00"), "2026-11-21T22:00:00Z"), heure));
        assertFalse(scope.isToday(record(Map.of("jour", "08:00"), "2026-11-21T20:00:00Z"), heure));
    }

    @Test
    void shouldCompareStoredDay_forDateColumns() {
        ModuleColumn jour = column(ColumnType.DATE);

        assertTrue(scope.isToday(record(Map.of("jour", "2026-11-22"), "2026-11-01T08:00:00Z"), jour));
        assertFalse(scope.isToday(record(Map.of("jour", "2026-11-21"), "2026-11-22T08:00:00Z"), jour));
    }

    @Test
    void shouldCompareDayPart_forDateTimeColumns() {
        assertTrue(scope.isToday(record(Map.of("jour", "2026-11-22T17:45"), "2026-11-01T08:00:00Z"),
                column(ColumnType.DATETIME)));
    }

    @Test
    void shouldBeFalse_whenValueIsMissingInvalidOrColumnIsNotTemporal() {
        assertFalse(scope.isToday(record(Map.of(), "2026-11-22T08:00:00Z"), column(ColumnType.DATE)));
        assertFalse(scope.isToday(record(Map.of("jour", "bientôt"), "2026-11-22T08:00:00Z"), column(ColumnType.DATE)));
        assertFalse(scope.isToday(record(Map.of("jour", "2026-11-22"), "2026-11-22T08:00:00Z"), column(ColumnType.TEXT)));
    }

    private ModuleColumn column(ColumnType type) {
        return DefinitionFixtures.column("jour", "Jour", type, false, null);
    }

    private ModuleRecord record(Map<String, Object> values, String createdAt) {
        return RecordFixtures.record(DefinitionFixtures.livraisons(), 1, "a_preparer", values, Instant.parse(createdAt));
    }
}

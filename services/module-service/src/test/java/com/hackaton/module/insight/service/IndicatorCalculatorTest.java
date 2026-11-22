package com.hackaton.module.insight.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.definition.entity.IndicatorKind;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.insight.dto.IndicatorValueResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.TestClocks;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class IndicatorCalculatorTest {

    private static final Instant TODAY = Instant.parse("2026-11-22T05:00:00Z");
    private static final Instant YESTERDAY = Instant.parse("2026-11-21T05:00:00Z");

    private final IndicatorCalculator calculator = new IndicatorCalculator(new TodayScope(TestClocks.SUNDAY_NOON));
    private final ModuleDefinition definition = DefinitionFixtures.livraisons();
    private final Map<String, ModuleColumn> columns = definition.getColumns().stream()
            .collect(Collectors.toMap(ModuleColumn::getKey, Function.identity()));
    private final List<ModuleRecord> records = List.of(
            record(1, "livree", TODAY, "Toky Rakoto", 10000),
            record(2, "en_route", TODAY, "Toky Rakoto", 20000),
            record(3, "en_route", YESTERDAY, "Hery Rabe", 5000),
            record(4, "a_preparer", TODAY, null, 7000),
            record(5, "livree", YESTERDAY, "Hery Rabe", 3000));

    @Test
    void shouldComputeRatioOfToday_withDenominatorOfTodayRecords() {
        IndicatorValueResponse ratio = calculator.compute(definition.getIndicators().get(0), columns, records);

        assertEquals(IndicatorKind.RATIO_TODAY, ratio.kind());
        assertEquals(BigDecimal.ONE, ratio.value());
        assertEquals(3L, ratio.total());
        assertFalse(ratio.money());
    }

    @Test
    void shouldCountRecordsInStatuses_andListDistinctDetailLabels() {
        IndicatorValueResponse count = calculator.compute(definition.getIndicators().get(1), columns, records);

        assertEquals(BigDecimal.valueOf(2), count.value());
        assertNull(count.total());
        assertEquals(List.of("Toky Rakoto", "Hery Rabe"), count.detail());
    }

    @Test
    void shouldSumMoneyColumn_overRecordsNotYetDelivered() {
        IndicatorValueResponse sum = calculator.compute(definition.getIndicators().get(2), columns, records);

        assertEquals(0, new BigDecimal("32000").compareTo(sum.value()));
        assertTrue(sum.money());
        assertEquals(List.of(), sum.detail());
    }

    @Test
    void shouldCountEverything_whenIndicatorHasNoStatus() {
        var all = DefinitionFixtures.indicator("tout", IndicatorKind.COUNT, List.of(), null, null, null);

        assertEquals(BigDecimal.valueOf(5), calculator.compute(all, columns, records).value());
        assertEquals(BigDecimal.ZERO, calculator.compute(all, columns, List.of()).value());
    }

    private static ModuleRecord record(long id, String status, Instant createdAt, String driver, int amount) {
        Map<String, Object> values = new HashMap<>(Map.of("montant", amount));
        if (driver != null) {
            values.put("livreur", RecordFixtures.ref(id, driver));
        }
        return RecordFixtures.record(DefinitionFixtures.livraisons(), id, status, values, createdAt);
    }
}

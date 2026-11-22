package com.hackaton.module.record.service.reader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.TestClocks;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RecordSearchFilterTest {

    private final RecordSearchFilter filter = new RecordSearchFilter();
    private final ModuleDefinition definition = DefinitionFixtures.livraisons();
    private final ModuleRecord record = RecordFixtures.record(definition, 1, "a_preparer", Map.of(
            "client", RecordFixtures.ref(7, "Hery Rabé"), "quartier", "Analakely",
            "montant", new BigDecimal("25000")), TestClocks.NOW);

    @Test
    void shouldMatchEverything_whenSearchIsBlank() {
        assertTrue(filter.matches(record, definition.getColumns(), null));
        assertTrue(filter.matches(record, definition.getColumns(), "  "));
    }

    @Test
    void shouldMatchReferenceLabel_ignoringCaseAndAccents() {
        assertTrue(filter.matches(record, definition.getColumns(), "hery rabe"));
    }

    @Test
    void shouldMatchTextPart() {
        assertTrue(filter.matches(record, definition.getColumns(), "LAKE"));
    }

    @Test
    void shouldNotMatchNumbers_orAbsentText() {
        assertFalse(filter.matches(record, definition.getColumns(), "25000"));
        assertFalse(filter.matches(record, definition.getColumns(), "Toamasina"));
    }
}

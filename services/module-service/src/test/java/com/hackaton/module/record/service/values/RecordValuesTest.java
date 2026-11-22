package com.hackaton.module.record.service.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.support.RecordFixtures;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RecordValuesTest {

    @Test
    void shouldTreatNullBlankAndEmptyMapAsBlank() {
        assertTrue(RecordValues.isBlank(null));
        assertTrue(RecordValues.isBlank("  "));
        assertTrue(RecordValues.isBlank(Map.of()));
        assertFalse(RecordValues.isBlank(0));
    }

    @Test
    void shouldReadLabel_fromReferenceOrPlainValue() {
        assertEquals(Optional.of("Hery Rabe"), RecordValues.label(RecordFixtures.ref(7, "Hery Rabe")));
        assertEquals(Optional.of("Isotry"), RecordValues.label("Isotry"));
        assertEquals(Optional.empty(), RecordValues.label(null));
    }

    @Test
    void shouldReadPositiveWholeId_fromMapNumberOrText() {
        assertEquals(Optional.of(7L), RecordValues.referenceId(Map.of("id", 7)));
        assertEquals(Optional.of(8L), RecordValues.referenceId("8"));
        assertEquals(Optional.of(9L), RecordValues.referenceId(9.0));
    }

    @Test
    void shouldRejectIds_thatAreNotPositiveWholeNumbers() {
        assertEquals(Optional.empty(), RecordValues.referenceId(Map.of("id", -2)));
        assertEquals(Optional.empty(), RecordValues.referenceId(Map.of("id", 1.5)));
        assertEquals(Optional.empty(), RecordValues.referenceId(Map.of("nom", "x")));
    }

    @Test
    void shouldParseNumber_fromNumberOrDecimalText() {
        assertEquals(Optional.of(new BigDecimal("1500")), RecordValues.number(1500));
        assertEquals(Optional.of(new BigDecimal("2.75")), RecordValues.number("2,75"));
        assertEquals(Optional.empty(), RecordValues.number("abc"));
        assertEquals(Optional.empty(), RecordValues.number(true));
    }
}

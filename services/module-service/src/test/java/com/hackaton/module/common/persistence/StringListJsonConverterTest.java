package com.hackaton.module.common.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class StringListJsonConverterTest {

    private final StringListJsonConverter converter = new StringListJsonConverter();

    @Test
    void shouldWriteJsonArray_whenListHasValues() {
        assertEquals("[\"Moto\",\"Vélo\"]", converter.convertToDatabaseColumn(List.of("Moto", "Vélo")));
    }

    @Test
    void shouldWriteNull_whenListIsEmptyOrNull() {
        assertNull(converter.convertToDatabaseColumn(List.of()));
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void shouldReadList_whenColumnHoldsJsonArray() {
        assertEquals(List.of("Moto", "Vélo"), converter.convertToEntityAttribute("[\"Moto\",\"Vélo\"]"));
    }

    @Test
    void shouldReadEmptyList_whenColumnIsNull() {
        assertEquals(List.of(), converter.convertToEntityAttribute(null));
    }

    @Test
    void shouldFail_whenColumnHoldsInvalidJson() {
        assertThrows(IllegalStateException.class, () -> converter.convertToEntityAttribute("{pas du json"));
    }
}

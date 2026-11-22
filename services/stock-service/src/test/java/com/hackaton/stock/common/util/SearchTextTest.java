package com.hackaton.stock.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SearchTextTest {

    @Test
    void shouldLowercaseTrimAndRemoveAccents() {
        assertEquals("epicerie fine", SearchText.normalize("  Épicerie Fine "));
    }

    @Test
    void shouldNormalizeNullToEmpty() {
        assertEquals("", SearchText.normalize(null));
    }

    @Test
    void shouldMatchAnyValue_whenSearchIsBlank() {
        assertTrue(SearchText.anyContains("  ", "Riz"));
    }

    @Test
    void shouldFindSearchInAnyValueIgnoringNulls() {
        assertTrue(SearchText.anyContains("hygi", "Savon", null, "Hygiène"));
        assertFalse(SearchText.anyContains("riz", "Savon", null));
    }
}

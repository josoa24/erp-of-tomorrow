package com.hackaton.module.definition.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SlugGeneratorTest {

    private final SlugGenerator generator = new SlugGenerator();

    @Test
    void shouldLowercaseName_whenNameIsSimple() {
        assertEquals("livraisons", generator.fromName("Livraisons"));
    }

    @Test
    void shouldStripAccentsAndJoinWords_whenNameHasSpacesAndAccents() {
        assertEquals("reparations-velos", generator.fromName("  Réparations  vélos "));
    }

    @Test
    void shouldDropSymbols_whenNameHasPunctuation() {
        assertEquals("rendez-vous-2026", generator.fromName("Rendez-vous (2026) !"));
    }

    @Test
    void shouldReturnEmpty_whenNameHasNoLetterOrDigit() {
        assertEquals("", generator.fromName("!!"));
    }

    @Test
    void shouldCutAt60Characters_withoutTrailingDash() {
        String slug = generator.fromName("a".repeat(59) + " bcd");

        assertEquals("a".repeat(59), slug);
    }
}

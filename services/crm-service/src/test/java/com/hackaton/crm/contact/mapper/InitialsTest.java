package com.hackaton.crm.contact.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InitialsTest {

    @Test
    void shouldTakeFirstLetterOfTwoFirstWords_whenNameHasSeveralWords() {
        assertEquals("HR", Initials.of("Hery Rabe Andriamanana"));
    }

    @Test
    void shouldTakeTwoFirstLetters_whenNameIsASingleWord() {
        assertEquals("SO", Initials.of("Socolait"));
    }

    @Test
    void shouldIgnoreExtraSpacesAndUppercaseAccents_whenNameIsLoose() {
        assertEquals("ÉS", Initials.of("  épicerie   soa "));
    }

    @Test
    void shouldKeepTheOnlyLetter_whenNameHasOneCharacter() {
        assertEquals("A", Initials.of("a"));
    }

    @Test
    void shouldReturnPlaceholder_whenNameIsBlank() {
        assertEquals("?", Initials.of("  "));
    }
}

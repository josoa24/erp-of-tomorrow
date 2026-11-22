package com.hackaton.module.adaptation.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class AdaptationCatalogTest {

    private final AdaptationCatalog catalog = new AdaptationCatalog();

    @Test
    void shouldMapEachFixedAdaptationToItsModule() {
        assertEquals(Optional.of("stock"), catalog.moduleOf("stock.lots-peremption"));
        assertEquals(Optional.of("ventes"), catalog.moduleOf("ventes.mobile-money-credit"));
        assertEquals(Optional.of("relations"), catalog.moduleOf("relations.nif-plafond-credit"));
        assertEquals(Optional.of("equipe"), catalog.moduleOf("equipe.avances-salaire"));
    }

    @Test
    void shouldReturnEmpty_whenAdaptationIsUnknownOrNull() {
        assertEquals(Optional.empty(), catalog.moduleOf("stock.inconnue"));
        assertEquals(Optional.empty(), catalog.moduleOf(null));
    }
}

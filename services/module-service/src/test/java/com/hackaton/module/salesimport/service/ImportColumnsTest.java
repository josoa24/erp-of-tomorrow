package com.hackaton.module.salesimport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.support.DefinitionFixtures;
import org.junit.jupiter.api.Test;

class ImportColumnsTest {

    @Test
    void shouldPickFirstSaleCustomerAndMoneyColumns() {
        assertEquals(new ImportColumns("vente", "client", "montant"), ImportColumns.of(DefinitionFixtures.livraisons()));
    }

    @Test
    void shouldLeaveOptionalColumnsEmpty_whenModuleHasOnlyASaleColumn() {
        ModuleDefinition definition = DefinitionFixtures.livraisons();
        definition.getColumns().removeIf(column -> !column.getKey().equals("vente"));

        assertEquals(new ImportColumns("vente", null, null), ImportColumns.of(definition));
    }

    @Test
    void shouldRefuseWithBusiness_whenModuleHasNoSaleColumn() {
        ModuleDefinition definition = DefinitionFixtures.livraisons();
        definition.getColumns().removeIf(column -> column.getKey().equals("vente"));

        assertThrows(BusinessException.class, () -> ImportColumns.of(definition));
    }
}

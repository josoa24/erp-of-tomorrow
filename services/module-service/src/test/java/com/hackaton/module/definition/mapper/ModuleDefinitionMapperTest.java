package com.hackaton.module.definition.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.hackaton.module.definition.dto.ColumnSpec;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.entity.ReferenceTarget;
import com.hackaton.module.support.RequestFixtures;
import com.hackaton.module.support.TestClocks;
import java.util.List;
import org.junit.jupiter.api.Test;

class ModuleDefinitionMapperTest {

    private final DefinitionPartsMapper partsMapper = new DefinitionPartsMapper();
    private final ModuleDefinitionMapper mapper = new ModuleDefinitionMapper(partsMapper);

    @Test
    void shouldLinkPartsInRequestOrder_whenMappingARequest() {
        ModuleDefinition definition = mapper.toEntity(RequestFixtures.livraisons(), "livraisons", "a@b.mg", TestClocks.NOW);

        assertEquals(List.of(0, 1, 2, 3, 4), definition.getColumns().stream().map(ModuleColumn::getPosition).toList());
        assertSame(definition, definition.getStatuses().get(2).getDefinition());
        assertEquals(2, definition.getIndicators().size());
        assertEquals("Suivi des livraisons du jour", definition.getDescription());
    }

    @Test
    void shouldStoreNullDescription_whenDescriptionIsBlank() {
        ModuleDefinitionRequest base = RequestFixtures.livraisons();
        ModuleDefinitionRequest request = new ModuleDefinitionRequest(" Livraisons ", "truck", "  ", base.columns(),
                base.statuses(), null);

        ModuleDefinition definition = mapper.toEntity(request, "livraisons", "a@b.mg", TestClocks.NOW);

        assertNull(definition.getDescription());
        assertEquals("Livraisons", definition.getName());
        assertEquals(0, definition.getIndicators().size());
    }

    @Test
    void shouldKeepChoicesAndTargetOnlyForMatchingTypes() {
        ModuleColumn text = partsMapper.toEntity(
                new ColumnSpec("note", "Note", ColumnType.TEXT, false, List.of("x"), ReferenceTarget.SALE), 0);
        ModuleColumn reference = partsMapper.toEntity(
                new ColumnSpec("vente", "Vente", ColumnType.REFERENCE, false, null, ReferenceTarget.SALE), 1);

        assertEquals(List.of(), text.getChoices());
        assertNull(text.getReferenceTarget());
        assertEquals(ReferenceTarget.SALE, reference.getReferenceTarget());
        assertNull(partsMapper.toSpec(reference).choices());
    }
}

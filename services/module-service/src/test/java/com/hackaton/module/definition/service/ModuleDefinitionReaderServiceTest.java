package com.hackaton.module.definition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.dto.ModuleSummary;
import com.hackaton.module.definition.mapper.DefinitionPartsMapper;
import com.hackaton.module.definition.mapper.ModuleDefinitionMapper;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.definition.service.reader.ModuleDefinitionReaderService;
import com.hackaton.module.support.DefinitionFixtures;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleDefinitionReaderServiceTest {

    @Mock
    private ModuleDefinitionRepository definitionRepository;

    private ModuleDefinitionReaderService reader;

    @BeforeEach
    void setUp() {
        reader = new ModuleDefinitionReaderService(definitionRepository, new DefinitionLookup(definitionRepository),
                new ModuleDefinitionMapper(new DefinitionPartsMapper()));
    }

    @Test
    void shouldListSummariesInCreationOrder() {
        when(definitionRepository.findAllByOrderByCreatedAtAscIdAsc()).thenReturn(List.of(DefinitionFixtures.livraisons()));

        List<ModuleSummary> summaries = reader.findAll();

        assertEquals(1, summaries.size());
        assertEquals("livraisons", summaries.get(0).slug());
        assertEquals("truck", summaries.get(0).icon());
    }

    @Test
    void shouldReturnFullDefinition_whenSlugExists() {
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(DefinitionFixtures.livraisons()));

        ModuleDefinitionResponse response = reader.findBySlug("livraisons");

        assertEquals(6, response.columns().size());
        assertEquals(3, response.statuses().size());
        assertTrue(response.statuses().get(2).finalStatus());
        assertEquals("livreur", response.indicators().get(1).detailColumn());
    }

    @Test
    void shouldThrowNotFound_whenSlugIsUnknown() {
        when(definitionRepository.findBySlug("inconnu")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reader.findBySlug("inconnu"));
    }
}

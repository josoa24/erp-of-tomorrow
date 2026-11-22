package com.hackaton.module.definition.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.ForbiddenException;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.deleter.ModuleDefinitionDeleterService;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.support.DefinitionFixtures;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleDefinitionDeleterServiceTest {

    private static final Caller ADMIN = new Caller("hery.rabe@nova.mg", "ADMIN");

    @Mock
    private ModuleDefinitionRepository definitionRepository;
    @Mock
    private ModuleRecordRepository recordRepository;

    private ModuleDefinitionDeleterService deleter;

    @BeforeEach
    void setUp() {
        deleter = new ModuleDefinitionDeleterService(new DefinitionLookup(definitionRepository), definitionRepository,
                recordRepository);
    }

    @Test
    void shouldDeleteDefinition_whenItHasNoRecord() {
        ModuleDefinition definition = DefinitionFixtures.livraisons();
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));
        when(recordRepository.existsByDefinitionId(10L)).thenReturn(false);

        deleter.delete("livraisons", ADMIN);

        verify(definitionRepository).delete(definition);
    }

    @Test
    void shouldRefuseWithBusiness_whenDefinitionHasRecords() {
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(DefinitionFixtures.livraisons()));
        when(recordRepository.existsByDefinitionId(10L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> deleter.delete("livraisons", ADMIN));
        verify(definitionRepository, never()).delete(any());
    }

    @Test
    void shouldThrowNotFound_whenSlugIsUnknown() {
        when(definitionRepository.findBySlug("inconnu")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deleter.delete("inconnu", ADMIN));
    }

    @Test
    void shouldRefuseWithForbidden_whenCallerIsNotAdmin() {
        assertThrows(ForbiddenException.class, () -> deleter.delete("livraisons", new Caller("toky@nova.mg", "RH")));

        verifyNoInteractions(definitionRepository, recordRepository);
    }
}

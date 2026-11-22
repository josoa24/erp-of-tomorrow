package com.hackaton.module.definition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.ConflictException;
import com.hackaton.module.common.exception.ForbiddenException;
import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.mapper.DefinitionPartsMapper;
import com.hackaton.module.definition.mapper.ModuleDefinitionMapper;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.creator.ModuleDefinitionCreatorService;
import com.hackaton.module.definition.service.creator.SlugGenerator;
import com.hackaton.module.definition.service.rules.DefinitionRules;
import com.hackaton.module.support.RequestFixtures;
import com.hackaton.module.support.TestClocks;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleDefinitionCreatorServiceTest {

    private static final Caller ADMIN = new Caller("hery.rabe@nova.mg", "ADMIN");

    @Mock
    private ModuleDefinitionRepository definitionRepository;
    @Mock
    private DefinitionRules definitionRules;

    private ModuleDefinitionCreatorService creator;

    @BeforeEach
    void setUp() {
        creator = new ModuleDefinitionCreatorService(definitionRepository, definitionRules, new SlugGenerator(),
                new ModuleDefinitionMapper(new DefinitionPartsMapper()), TestClocks.SUNDAY_NOON);
    }

    @Test
    void shouldSaveDefinitionWithSlugAuthorAndDate_whenAdminCreatesIt() {
        when(definitionRepository.existsBySlug("livraisons")).thenReturn(false);
        when(definitionRepository.save(any(ModuleDefinition.class))).thenAnswer(call -> call.getArgument(0));

        ModuleDefinitionResponse response = creator.create(RequestFixtures.livraisons(), ADMIN);

        assertEquals("livraisons", response.slug());
        assertEquals("hery.rabe@nova.mg", response.createdBy());
        assertEquals(TestClocks.NOW, response.createdAt());
        assertEquals(List.of("Moto", "Vélo"), response.columns().get(2).choices());
        assertEquals("a_preparer", response.statuses().get(0).key());
        verify(definitionRules).check(any());
    }

    @Test
    void shouldRefuseWithForbidden_whenCallerIsNotAdmin() {
        assertThrows(ForbiddenException.class,
                () -> creator.create(RequestFixtures.livraisons(), new Caller("rasoa.andria@nova.mg", "VENTE")));

        verifyNoInteractions(definitionRepository, definitionRules);
    }

    @Test
    void shouldRefuseWithConflict_whenSlugAlreadyExists() {
        when(definitionRepository.existsBySlug("livraisons")).thenReturn(true);

        assertThrows(ConflictException.class, () -> creator.create(RequestFixtures.livraisons(), ADMIN));
        verify(definitionRepository, never()).save(any());
    }

    @Test
    void shouldRefuseWithConflict_whenNameMatchesABuiltInModule() {
        assertThrows(ConflictException.class, () -> creator.create(named("Équipe"), ADMIN));
    }

    @Test
    void shouldRefuseWithValidation_whenNameHasNoLetter() {
        assertThrows(InvalidInputException.class, () -> creator.create(named("??"), ADMIN));
    }

    private ModuleDefinitionRequest named(String name) {
        ModuleDefinitionRequest base = RequestFixtures.livraisons();
        return new ModuleDefinitionRequest(name, base.icon(), base.description(), base.columns(), base.statuses(),
                base.indicators());
    }
}

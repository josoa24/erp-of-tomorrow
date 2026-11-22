package com.hackaton.module.adaptation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.module.adaptation.catalog.AdaptationCatalog;
import com.hackaton.module.adaptation.dto.AdaptationRequest;
import com.hackaton.module.adaptation.dto.AdaptationResponse;
import com.hackaton.module.adaptation.entity.AdaptationActivation;
import com.hackaton.module.adaptation.mapper.AdaptationMapper;
import com.hackaton.module.adaptation.repository.AdaptationActivationRepository;
import com.hackaton.module.adaptation.service.creator.AdaptationCreatorService;
import com.hackaton.module.common.exception.ConflictException;
import com.hackaton.module.common.exception.ForbiddenException;
import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.common.security.Caller;
import com.hackaton.module.support.TestClocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdaptationCreatorServiceTest {

    private static final Caller ADMIN = new Caller("hery.rabe@nova.mg", "ADMIN");
    private static final String LOTS = "stock.lots-peremption";

    @Mock
    private AdaptationActivationRepository repository;

    private final AdaptationMapper mapper = new AdaptationMapper();

    @Test
    void shouldActivateKnownAdaptation_withAuthorAndDate() {
        when(repository.existsByAdaptationKey(LOTS)).thenReturn(false);
        when(repository.save(any(AdaptationActivation.class))).thenAnswer(call -> call.getArgument(0));

        AdaptationResponse response = creator().activate(new AdaptationRequest("stock", LOTS), ADMIN);

        assertEquals("stock", response.moduleKey());
        assertEquals("hery.rabe@nova.mg", response.activatedBy());
        assertEquals(TestClocks.NOW, response.activatedAt());
    }

    @Test
    void shouldRefuse_whenAdaptationIsUnknownOrInAnotherModule() {
        assertThrows(InvalidInputException.class,
                () -> creator().activate(new AdaptationRequest("stock", "stock.inconnue"), ADMIN));
        assertThrows(InvalidInputException.class, () -> creator().activate(new AdaptationRequest("ventes", LOTS), ADMIN));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldRefuseWithConflict_whenAlreadyActive() {
        when(repository.existsByAdaptationKey(LOTS)).thenReturn(true);

        assertThrows(ConflictException.class, () -> creator().activate(new AdaptationRequest("stock", LOTS), ADMIN));
    }

    @Test
    void shouldRefuseWithForbidden_whenNotAdmin() {
        Caller seller = new Caller("rasoa.andria@nova.mg", "VENTE");

        assertThrows(ForbiddenException.class, () -> creator().activate(new AdaptationRequest("stock", LOTS), seller));
        verifyNoInteractions(repository);
    }

    private AdaptationCreatorService creator() {
        return new AdaptationCreatorService(repository, new AdaptationCatalog(), mapper, TestClocks.SUNDAY_NOON);
    }
}

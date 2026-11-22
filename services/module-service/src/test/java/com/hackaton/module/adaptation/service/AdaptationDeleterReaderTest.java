package com.hackaton.module.adaptation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.module.adaptation.entity.AdaptationActivation;
import com.hackaton.module.adaptation.mapper.AdaptationMapper;
import com.hackaton.module.adaptation.repository.AdaptationActivationRepository;
import com.hackaton.module.adaptation.service.deleter.AdaptationDeleterService;
import com.hackaton.module.adaptation.service.reader.AdaptationReaderService;
import com.hackaton.module.common.exception.ForbiddenException;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.common.security.Caller;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdaptationDeleterReaderTest {

    private static final Caller ADMIN = new Caller("hery.rabe@nova.mg", "ADMIN");
    private static final String LOTS = "stock.lots-peremption";

    @Mock
    private AdaptationActivationRepository repository;

    private final AdaptationMapper mapper = new AdaptationMapper();

    @Test
    void shouldRefuseDeactivation_whenNotAdmin() {
        assertThrows(ForbiddenException.class,
                () -> new AdaptationDeleterService(repository).deactivate(LOTS, new Caller("toky@nova.mg", "RH")));
        verifyNoInteractions(repository);
    }

    @Test
    void shouldDeactivate_whenAdaptationIsActive() {
        AdaptationActivation activation = new AdaptationActivation();
        when(repository.findByAdaptationKey(LOTS)).thenReturn(Optional.of(activation));

        new AdaptationDeleterService(repository).deactivate(LOTS, ADMIN);

        verify(repository).delete(activation);
    }

    @Test
    void shouldThrowNotFound_whenDeactivatingInactiveAdaptation() {
        when(repository.findByAdaptationKey(LOTS)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> new AdaptationDeleterService(repository).deactivate(LOTS, ADMIN));
    }

    @Test
    void shouldListActivations() {
        AdaptationActivation activation = new AdaptationActivation();
        activation.setAdaptationKey(LOTS);
        when(repository.findAllByOrderByActivatedAtAscIdAsc()).thenReturn(List.of(activation));

        assertEquals(LOTS, new AdaptationReaderService(repository, mapper).findAll().get(0).adaptationKey());
    }
}

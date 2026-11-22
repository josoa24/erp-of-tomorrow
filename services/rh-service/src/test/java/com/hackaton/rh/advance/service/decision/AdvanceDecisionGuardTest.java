package com.hackaton.rh.advance.service.decision;

import static com.hackaton.rh.advance.AdvanceTestData.NOVEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.advance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.common.exception.ForbiddenException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.common.security.Actor;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvanceDecisionGuardTest {

    private static final Actor ADMIN = new Actor(1L, "ADMIN");

    @Mock
    private SalaryAdvanceRepository advanceRepository;

    @InjectMocks
    private AdvanceDecisionGuard guard;

    @Test
    void shouldReturnDraft_whenAdminDecides() {
        when(advanceRepository.findById(3L))
                .thenReturn(Optional.of(advance(3L, 7L, NOVEMBER, "80000", AdvanceStatus.DRAFT)));

        assertEquals(3L, guard.draftToDecide(3L, ADMIN).getId());
    }

    @Test
    void shouldRefuseWithoutReading_whenActorIsNotAdmin() {
        assertThrows(ForbiddenException.class, () -> guard.draftToDecide(3L, new Actor(2L, "RH")));
        verifyNoInteractions(advanceRepository);
    }

    @Test
    void shouldThrowNotFound_whenAdvanceDoesNotExist() {
        when(advanceRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> guard.draftToDecide(9L, ADMIN));
    }

    @ParameterizedTest
    @EnumSource(value = AdvanceStatus.class, names = {"VALIDATED", "REJECTED"})
    void shouldRefuseAsImmutable_whenAdvanceIsAlreadyDecided(AdvanceStatus decided) {
        when(advanceRepository.findById(3L)).thenReturn(Optional.of(advance(3L, 7L, NOVEMBER, "80000", decided)));

        assertThrows(ImmutableResourceException.class, () -> guard.draftToDecide(3L, ADMIN));
    }
}

package com.hackaton.rh.advance.service.decision;

import static com.hackaton.rh.advance.AdvanceTestData.NOVEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.advance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.advance.AdvanceTestData;
import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.mapper.AdvanceMapper;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.common.exception.ForbiddenException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.common.security.Actor;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvanceRejecterServiceTest {

    private static final Actor ADMIN = new Actor(1L, "ADMIN");

    @Mock
    private SalaryAdvanceRepository advanceRepository;
    @Mock
    private EmployeeNameDirectory nameDirectory;

    private AdvanceRejecterService rejecter;

    @BeforeEach
    void setUp() {
        rejecter = new AdvanceRejecterService(new AdvanceDecisionGuard(advanceRepository), advanceRepository,
                nameDirectory, new AdvanceMapper(), AdvanceTestData.clockAt("2026-11-20T07:00:00Z"));
    }

    @Test
    void shouldRejectThenLockAdvance_whenAdminRejectsDraft() {
        when(advanceRepository.findById(3L))
                .thenReturn(Optional.of(advance(3L, 7L, NOVEMBER, "80000", AdvanceStatus.DRAFT)));
        when(advanceRepository.save(any(SalaryAdvance.class))).thenAnswer(call -> call.getArgument(0));
        when(nameDirectory.nameOf(7L)).thenReturn("Toky Rakoto");

        AdvanceResponse response = rejecter.reject(3L, ADMIN);

        assertEquals(AdvanceStatus.REJECTED, response.status());
        assertEquals(1L, response.decidedBy());
        assertEquals("Toky Rakoto", response.employeeName());
        assertThrows(ImmutableResourceException.class, () -> rejecter.reject(3L, ADMIN));
    }

    @Test
    void shouldRefuseWithoutSaving_whenActorIsHumanResources() {
        assertThrows(ForbiddenException.class, () -> rejecter.reject(3L, new Actor(2L, "RH")));
        verify(advanceRepository, never()).save(any());
    }
}

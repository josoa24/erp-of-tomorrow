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
import com.hackaton.rh.advance.service.cap.AdvanceCapPolicy;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.security.Actor;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvanceValidatorServiceTest {

    private static final String NOW = "2026-11-20T07:00:00Z";
    private static final Actor ADMIN = new Actor(1L, "ADMIN");

    @Mock
    private SalaryAdvanceRepository advanceRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    private SalaryAdvance draft;
    private AdvanceValidatorService validator;

    @BeforeEach
    void setUp() {
        draft = advance(3L, 7L, NOVEMBER, "80000", AdvanceStatus.DRAFT);
        when(advanceRepository.findById(3L)).thenReturn(Optional.of(draft));
        when(employeeRepository.findLockedById(7L)).thenReturn(Optional.of(AdvanceTestData.toky()));
        validator = new AdvanceValidatorService(new AdvanceDecisionGuard(advanceRepository), advanceRepository,
                employeeRepository, new AdvanceCapPolicy(new BigDecimal("30")), new AdvanceMapper(),
                AdvanceTestData.clockAt(NOW));
    }

    @Test
    void shouldValidateAndRecordDecider_whenValidatedTotalStaysWithinCap() {
        stubValidated("160000");
        when(advanceRepository.save(any(SalaryAdvance.class))).thenAnswer(call -> call.getArgument(0));

        AdvanceResponse response = validator.validate(3L, ADMIN);

        assertEquals(AdvanceStatus.VALIDATED, response.status());
        assertEquals(1L, response.decidedBy());
        assertEquals(Instant.parse(NOW), response.decidedAt());
    }

    @Test
    void shouldRefuseAndKeepDraft_whenCapIsExceededAtValidation() {
        stubValidated("160001");

        assertThrows(BusinessException.class, () -> validator.validate(3L, ADMIN));
        assertEquals(AdvanceStatus.DRAFT, draft.getStatus());
        verify(advanceRepository, never()).save(any());
    }

    private void stubValidated(String amount) {
        when(advanceRepository.sumAmount(7L, NOVEMBER, List.of(AdvanceStatus.VALIDATED)))
                .thenReturn(new BigDecimal(amount));
    }
}

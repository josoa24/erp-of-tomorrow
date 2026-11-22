package com.hackaton.rh.advance.service.creator;

import static com.hackaton.rh.advance.AdvanceTestData.DECEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.NOVEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.advance.AdvanceTestData;
import com.hackaton.rh.advance.dto.AdvanceCreateRequest;
import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.mapper.AdvanceMapper;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.advance.service.cap.AdvanceCapPolicy;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvanceCreatorServiceTest {

    @Mock
    private SalaryAdvanceRepository advanceRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void shouldCreateDraftInLocalMonth_whenUtcIsStillPreviousMonth() {
        stubCommitted(DECEMBER, "0");
        when(advanceRepository.save(any(SalaryAdvance.class))).thenAnswer(call -> call.getArgument(0));

        AdvanceResponse response = creatorAt("2026-11-30T21:30:00Z").create(request("150000"));

        assertEquals(YearMonth.of(2026, 12), response.month());
        assertEquals(AdvanceStatus.DRAFT, response.status());
        assertEquals("Toky Rakoto", response.employeeName());
    }

    @Test
    void shouldKeepCurrentMonth_whenLocalTimeIsLastSecondOfMonth() {
        stubCommitted(NOVEMBER, "0");
        when(advanceRepository.save(any(SalaryAdvance.class))).thenAnswer(call -> call.getArgument(0));

        AdvanceResponse response = creatorAt("2026-11-30T20:59:59Z").create(request("150000"));

        assertEquals(YearMonth.of(2026, 11), response.month());
    }

    @Test
    void shouldRefuseWithoutSaving_whenMonthTotalWouldExceedCap() {
        stubCommitted(NOVEMBER, "150000");

        assertThrows(BusinessException.class, () -> creatorAt("2026-11-15T09:00:00Z").create(request("90001")));
        verify(advanceRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFound_whenEmployeeDoesNotExist() {
        when(employeeRepository.findLockedById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> creatorAt("2026-11-15T09:00:00Z").create(request("1000")));
    }

    private void stubCommitted(LocalDate month, String committed) {
        when(employeeRepository.findLockedById(7L)).thenReturn(Optional.of(AdvanceTestData.toky()));
        when(advanceRepository.sumAmount(7L, month, AdvanceStatus.COMMITTED)).thenReturn(new BigDecimal(committed));
    }

    private AdvanceCreatorService creatorAt(String instant) {
        return new AdvanceCreatorService(advanceRepository, employeeRepository,
                new AdvanceCapPolicy(new BigDecimal("30")), new AdvanceMapper(), AdvanceTestData.clockAt(instant));
    }

    private AdvanceCreateRequest request(String amount) {
        return new AdvanceCreateRequest(7L, new BigDecimal(amount), "Frais de scolarité");
    }
}

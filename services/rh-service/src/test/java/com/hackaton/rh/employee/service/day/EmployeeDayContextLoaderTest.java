package com.hackaton.rh.employee.service.day;

import static com.hackaton.rh.advance.AdvanceTestData.DECEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.advance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.rh.advance.AdvanceTestData;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.department.entity.Department;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeDayContextLoaderTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private SalaryAdvanceRepository advanceRepository;

    @Test
    void shouldUseLocalDayAndMonth_whenUtcIsStillPreviousDay() {
        when(departmentRepository.findAll()).thenReturn(List.of(department(1L, "Logistique")));
        when(leaveRepository.findEmployeeIdsActiveOn(LeaveStatus.VALIDATED, LocalDate.of(2026, 12, 1)))
                .thenReturn(List.of(8L));
        when(advanceRepository.findAllByMonthAndStatusIn(DECEMBER, AdvanceStatus.COMMITTED)).thenReturn(List.of(
                advance(1L, 7L, DECEMBER, "100000", AdvanceStatus.VALIDATED),
                advance(2L, 7L, DECEMBER, "50000", AdvanceStatus.VALIDATED),
                advance(3L, 8L, DECEMBER, "80000", AdvanceStatus.DRAFT)));

        EmployeeDayContext day = new EmployeeDayContextLoader(departmentRepository, leaveRepository,
                advanceRepository, AdvanceTestData.clockAt("2026-11-30T22:00:00Z")).load();

        assertEquals(Map.of(1L, "Logistique"), day.departmentNames());
        assertEquals(Set.of(8L), day.onLeave());
        assertEquals(new BigDecimal("150000"), day.monthAdvances().get(7L));
        assertEquals(Set.of(8L), day.pendingAdvances());
    }

    private Department department(Long id, String name) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        return department;
    }
}

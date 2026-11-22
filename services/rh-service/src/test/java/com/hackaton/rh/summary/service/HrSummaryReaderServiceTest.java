package com.hackaton.rh.summary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import com.hackaton.rh.summary.dto.DepartmentHeadcount;
import com.hackaton.rh.summary.dto.HrSummaryResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HrSummaryReaderServiceTest {

    private static final Clock UTC_EVENING =
            Clock.fixed(Instant.parse("2026-11-21T22:00:00Z"), ZoneId.of("Indian/Antananarivo"));

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private LeaveRequestRepository leaveRepository;

    private HrSummaryReaderService reader;

    @BeforeEach
    void setUp() {
        when(departmentRepository.headcountByDepartment()).thenReturn(List.of(
                new DepartmentHeadcount(1L, "Logistique", 12L), new DepartmentHeadcount(2L, "Stock", 0L)));
        reader = new HrSummaryReaderService(employeeRepository, departmentRepository, leaveRepository, UTC_EVENING);
    }

    @Test
    void shouldCountLeavesOfLocalDay_whenUtcDateIsStillYesterday() {
        when(leaveRepository.countActiveOn(LeaveStatus.VALIDATED, LocalDate.of(2026, 11, 22))).thenReturn(3L);

        HrSummaryResponse summary = reader.summarize();

        assertEquals(3, summary.onLeaveToday());
    }

    @Test
    void shouldCountDraftLeavesAsPending_whenSummarizing() {
        when(leaveRepository.countByStatus(LeaveStatus.DRAFT)).thenReturn(4L);

        assertEquals(4, reader.summarize().pendingLeaves());
    }

    @Test
    void shouldIgnoreEmptyDepartments_whenCountingStaffedDepartments() {
        when(employeeRepository.count()).thenReturn(12L);

        HrSummaryResponse summary = reader.summarize();

        assertEquals(1, summary.departmentsWithEmployees());
        assertEquals(2, summary.headcountByDepartment().size());
    }
}

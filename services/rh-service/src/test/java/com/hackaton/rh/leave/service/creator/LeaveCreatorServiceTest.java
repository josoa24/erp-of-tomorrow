package com.hackaton.rh.leave.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.dto.LeaveCreateRequest;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveCreatorServiceTest {

    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    private LeaveCreatorService creator;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setId(7L);
        employee.setFirstName("Nirina");
        employee.setLastName("Rabe");
        employee.setLeaveBalance(10);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        creator = new LeaveCreatorService(leaveRepository, employeeRepository, new LeaveMapper());
    }

    @Test
    void shouldCreateDraftLeaveWithInclusiveDayCount() {
        when(leaveRepository.existsOverlap(any(), anyList(), any(), any())).thenReturn(false);
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(call -> call.getArgument(0));

        LeaveResponse response = creator.create(request(1, 5, "repos"));

        assertEquals(5, response.days());
        assertEquals(LeaveStatus.DRAFT, response.status());
        assertEquals("Nirina Rabe", response.employeeName());
    }

    @Test
    void shouldRefuseLeave_whenBalanceIsInsufficient() {
        assertThrows(BusinessException.class, () -> creator.create(request(1, 20, null)));
        verify(leaveRepository, never()).save(any());
    }

    @Test
    void shouldRefuseLeave_whenEndIsBeforeStart() {
        assertThrows(BusinessException.class, () -> creator.create(request(5, 1, null)));
    }

    @Test
    void shouldRefuseLeave_whenPeriodOverlapsAnotherRequest() {
        when(leaveRepository.existsOverlap(any(), anyList(), any(), any())).thenReturn(true);

        assertThrows(BusinessException.class, () -> creator.create(request(1, 3, null)));
    }

    private LeaveCreateRequest request(int startDay, int endDay, String reason) {
        return new LeaveCreateRequest(7L, LocalDate.of(2026, 12, startDay), LocalDate.of(2026, 12, endDay), reason);
    }
}

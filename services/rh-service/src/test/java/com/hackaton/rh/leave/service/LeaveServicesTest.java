package com.hackaton.rh.leave.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.dto.LeaveCreateRequest;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import com.hackaton.rh.leave.service.creator.LeaveCreatorService;
import com.hackaton.rh.leave.service.decision.LeaveRejecterService;
import com.hackaton.rh.leave.service.decision.LeaveValidatorService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveServicesTest {

    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    private final LeaveMapper mapper = new LeaveMapper();
    private Employee employee;

    private LeaveRequest draftLeave(int days) {
        LeaveRequest leave = new LeaveRequest();
        leave.setId(1L);
        leave.setEmployeeId(7L);
        leave.setStartDate(LocalDate.of(2026, 12, 1));
        leave.setEndDate(LocalDate.of(2026, 12, days));
        leave.setDays(days);
        return leave;
    }

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(7L);
        employee.setLeaveBalance(10);
    }

    @Test
    void shouldCreateDraftLeaveWithInclusiveDayCount() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(leaveRepository.existsOverlap(any(), anyList(), any(), any())).thenReturn(false);
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(call -> call.getArgument(0));
        LeaveCreatorService creator = new LeaveCreatorService(leaveRepository, employeeRepository, mapper);

        LeaveResponse response = creator.create(
                new LeaveCreateRequest(7L, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5), "repos"));

        assertEquals(5, response.days());
        assertEquals(LeaveStatus.DRAFT, response.status());
    }

    @Test
    void shouldRefuseLeave_whenBalanceIsInsufficient() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        LeaveCreatorService creator = new LeaveCreatorService(leaveRepository, employeeRepository, mapper);

        assertThrows(BusinessException.class, () -> creator.create(
                new LeaveCreateRequest(7L, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 20), null)));
        verify(leaveRepository, never()).save(any());
    }

    @Test
    void shouldRefuseLeave_whenEndIsBeforeStart() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        LeaveCreatorService creator = new LeaveCreatorService(leaveRepository, employeeRepository, mapper);

        assertThrows(BusinessException.class, () -> creator.create(
                new LeaveCreateRequest(7L, LocalDate.of(2026, 12, 5), LocalDate.of(2026, 12, 1), null)));
    }

    @Test
    void shouldRefuseLeave_whenPeriodOverlapsAnotherRequest() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(leaveRepository.existsOverlap(any(), anyList(), any(), any())).thenReturn(true);
        LeaveCreatorService creator = new LeaveCreatorService(leaveRepository, employeeRepository, mapper);

        assertThrows(BusinessException.class, () -> creator.create(
                new LeaveCreateRequest(7L, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 3), null)));
    }

    @Test
    void shouldDeductBalanceAndLock_whenLeaveIsValidated() {
        LeaveRequest leave = draftLeave(4);
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(call -> call.getArgument(0));

        LeaveResponse response = new LeaveValidatorService(leaveRepository, employeeRepository, mapper).validate(1L);

        assertEquals(LeaveStatus.VALIDATED, response.status());
        assertEquals(6, employee.getLeaveBalance());
    }

    @Test
    void shouldRefuseValidation_whenLeaveIsAlreadyProcessed() {
        LeaveRequest leave = draftLeave(4);
        leave.setStatus(LeaveStatus.VALIDATED);
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));

        assertThrows(ImmutableResourceException.class,
                () -> new LeaveValidatorService(leaveRepository, employeeRepository, mapper).validate(1L));
        assertEquals(10, employee.getLeaveBalance());
    }

    @Test
    void shouldRefuseValidation_whenBalanceDroppedSinceRequest() {
        LeaveRequest leave = draftLeave(4);
        employee.setLeaveBalance(2);
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        assertThrows(BusinessException.class,
                () -> new LeaveValidatorService(leaveRepository, employeeRepository, mapper).validate(1L));
        assertEquals(LeaveStatus.DRAFT, leave.getStatus());
    }

    @Test
    void shouldRejectWithoutTouchingBalance_andLockTheRequest() {
        LeaveRequest leave = draftLeave(4);
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(call -> call.getArgument(0));
        LeaveRejecterService rejecter = new LeaveRejecterService(leaveRepository, mapper);

        LeaveResponse response = rejecter.reject(1L);

        assertEquals(LeaveStatus.REJECTED, response.status());
        assertEquals(10, employee.getLeaveBalance());
        assertThrows(ImmutableResourceException.class, () -> rejecter.reject(1L));
    }
}

package com.hackaton.rh.leave.service.decision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
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
class LeaveValidatorServiceTest {

    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    private LeaveValidatorService validator;
    private Employee employee;
    private LeaveRequest leave;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(7L);
        employee.setLeaveBalance(10);
        leave = new LeaveRequest();
        leave.setId(1L);
        leave.setEmployeeId(7L);
        leave.setStartDate(LocalDate.of(2026, 12, 1));
        leave.setEndDate(LocalDate.of(2026, 12, 4));
        leave.setDays(4);
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        validator = new LeaveValidatorService(leaveRepository, employeeRepository, new LeaveMapper());
    }

    @Test
    void shouldDeductBalanceAndLock_whenLeaveIsValidated() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(call -> call.getArgument(0));

        LeaveResponse response = validator.validate(1L);

        assertEquals(LeaveStatus.VALIDATED, response.status());
        assertEquals(6, employee.getLeaveBalance());
    }

    @Test
    void shouldRefuseValidation_whenLeaveIsAlreadyProcessed() {
        leave.setStatus(LeaveStatus.VALIDATED);

        assertThrows(ImmutableResourceException.class, () -> validator.validate(1L));
        assertEquals(10, employee.getLeaveBalance());
    }

    @Test
    void shouldRefuseValidation_whenBalanceDroppedSinceRequest() {
        employee.setLeaveBalance(2);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        assertThrows(BusinessException.class, () -> validator.validate(1L));
        assertEquals(LeaveStatus.DRAFT, leave.getStatus());
    }
}

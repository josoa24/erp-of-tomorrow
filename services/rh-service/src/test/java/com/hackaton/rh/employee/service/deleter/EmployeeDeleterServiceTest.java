package com.hackaton.rh.employee.service.deleter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.attendance.repository.AttendanceRepository;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeDeleterServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private EmployeeDeleterService service;

    @Test
    void shouldRefuseEmployeeDeletion_whenHistoryExists() {
        when(employeeRepository.existsById(7L)).thenReturn(true);
        when(leaveRepository.existsByEmployeeId(7L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.delete(7L));
        verify(employeeRepository, never()).deleteById(any());
    }
}

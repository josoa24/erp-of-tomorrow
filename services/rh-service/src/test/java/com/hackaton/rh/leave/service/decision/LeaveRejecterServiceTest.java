package com.hackaton.rh.leave.service.decision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveRejecterServiceTest {

    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeNameDirectory nameDirectory;

    @Test
    void shouldRejectWithoutTouchingBalance_andLockTheRequest() {
        LeaveRequest leave = new LeaveRequest();
        leave.setId(1L);
        leave.setEmployeeId(7L);
        leave.setStartDate(LocalDate.of(2026, 12, 1));
        leave.setEndDate(LocalDate.of(2026, 12, 4));
        leave.setDays(4);
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(call -> call.getArgument(0));
        when(nameDirectory.nameOf(7L)).thenReturn("Nirina Rabe");
        LeaveRejecterService rejecter = new LeaveRejecterService(leaveRepository, new LeaveMapper(), nameDirectory);

        LeaveResponse response = rejecter.reject(1L, 42L);

        assertEquals(LeaveStatus.REJECTED, response.status());
        assertEquals(42L, response.decidedBy());
        assertEquals("Nirina Rabe", response.employeeName());
        assertThrows(ImmutableResourceException.class, () -> rejecter.reject(1L, 42L));
        verifyNoInteractions(employeeRepository);
    }
}

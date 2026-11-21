package com.hackaton.rh.attendance.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.entity.Attendance;
import com.hackaton.rh.attendance.mapper.AttendanceMapper;
import com.hackaton.rh.attendance.repository.AttendanceRepository;
import com.hackaton.rh.attendance.service.checkin.AttendanceCheckInService;
import com.hackaton.rh.attendance.service.checkout.AttendanceCheckOutService;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttendanceServicesTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    private final AttendanceMapper mapper = new AttendanceMapper();

    private Attendance openAttendance() {
        Attendance attendance = new Attendance();
        attendance.setId(1L);
        attendance.setEmployeeId(7L);
        attendance.setWorkDate(LocalDate.now());
        attendance.setCheckIn(Instant.now().minusSeconds(3600));
        return attendance;
    }

    @Test
    void shouldOpenAttendance_onCheckIn() {
        when(employeeRepository.existsById(7L)).thenReturn(true);
        when(attendanceRepository.findFirstByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(7L))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(call -> call.getArgument(0));

        AttendanceResponse response =
                new AttendanceCheckInService(attendanceRepository, employeeRepository, mapper).checkIn(7L);

        assertNotNull(response.checkIn());
        assertNull(response.checkOut());
    }

    @Test
    void shouldRefuseCheckIn_whenAnAttendanceIsAlreadyOpen() {
        when(employeeRepository.existsById(7L)).thenReturn(true);
        when(attendanceRepository.findFirstByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(7L))
                .thenReturn(Optional.of(openAttendance()));

        assertThrows(BusinessException.class,
                () -> new AttendanceCheckInService(attendanceRepository, employeeRepository, mapper).checkIn(7L));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void shouldRefuseCheckIn_whenEmployeeDoesNotExist() {
        when(employeeRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> new AttendanceCheckInService(attendanceRepository, employeeRepository, mapper).checkIn(9L));
    }

    @Test
    void shouldCloseAttendanceAndComputeWorkedMinutes_onCheckOut() {
        when(attendanceRepository.findFirstByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(7L))
                .thenReturn(Optional.of(openAttendance()));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(call -> call.getArgument(0));

        AttendanceResponse response = new AttendanceCheckOutService(attendanceRepository, mapper).checkOut(7L);

        assertNotNull(response.checkOut());
        assertNotNull(response.workedMinutes());
    }

    @Test
    void shouldRefuseCheckOut_whenNoAttendanceIsOpen() {
        when(attendanceRepository.findFirstByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(7L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> new AttendanceCheckOutService(attendanceRepository, mapper).checkOut(7L));
    }
}

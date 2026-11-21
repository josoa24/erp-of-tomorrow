package com.hackaton.rh.attendance.service.checkin;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.entity.Attendance;
import com.hackaton.rh.attendance.mapper.AttendanceMapper;
import com.hackaton.rh.attendance.repository.AttendanceRepository;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceCheckInService implements AttendanceCheckIn {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public AttendanceResponse checkIn(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employé", employeeId);
        }
        if (attendanceRepository.findFirstByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(employeeId).isPresent()) {
            throw new BusinessException("Un pointage d'entrée est déjà ouvert pour cet employé");
        }
        Instant now = Instant.now();
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setCheckIn(now);
        attendance.setWorkDate(now.atZone(ZoneOffset.UTC).toLocalDate());
        return attendanceMapper.toResponse(attendanceRepository.save(attendance));
    }
}

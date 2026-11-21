package com.hackaton.rh.attendance.service.checkout;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.entity.Attendance;
import com.hackaton.rh.attendance.mapper.AttendanceMapper;
import com.hackaton.rh.attendance.repository.AttendanceRepository;
import com.hackaton.rh.common.exception.BusinessException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceCheckOutService implements AttendanceCheckOut {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public AttendanceResponse checkOut(Long employeeId) {
        Attendance attendance = attendanceRepository
                .findFirstByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(employeeId)
                .orElseThrow(() -> new BusinessException("Aucun pointage d'entrée ouvert pour cet employé"));
        attendance.setCheckOut(Instant.now());
        return attendanceMapper.toResponse(attendanceRepository.save(attendance));
    }
}

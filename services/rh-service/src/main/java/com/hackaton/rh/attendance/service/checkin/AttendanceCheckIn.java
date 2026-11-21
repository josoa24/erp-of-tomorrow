package com.hackaton.rh.attendance.service.checkin;

import com.hackaton.rh.attendance.dto.AttendanceResponse;

public interface AttendanceCheckIn {

    AttendanceResponse checkIn(Long employeeId);
}

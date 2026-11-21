package com.hackaton.rh.attendance.service.checkout;

import com.hackaton.rh.attendance.dto.AttendanceResponse;

public interface AttendanceCheckOut {

    AttendanceResponse checkOut(Long employeeId);
}

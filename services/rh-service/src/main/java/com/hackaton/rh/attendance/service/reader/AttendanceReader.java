package com.hackaton.rh.attendance.service.reader;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceReader {

    List<AttendanceResponse> search(Long employeeId, LocalDate date);
}

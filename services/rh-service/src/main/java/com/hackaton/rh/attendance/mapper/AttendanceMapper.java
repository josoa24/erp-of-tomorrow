package com.hackaton.rh.attendance.mapper;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.entity.Attendance;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceResponse toResponse(Attendance attendance) {
        Long worked = attendance.getCheckOut() == null
                ? null
                : Duration.between(attendance.getCheckIn(), attendance.getCheckOut()).toMinutes();
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getEmployeeId(),
                attendance.getWorkDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                worked);
    }
}

package com.hackaton.rh.attendance.service.reader;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.mapper.AttendanceMapper;
import com.hackaton.rh.attendance.repository.AttendanceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceReaderService implements AttendanceReader {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    public List<AttendanceResponse> search(Long employeeId, LocalDate date) {
        return attendanceRepository.findAllByOrderByCheckInDesc().stream()
                .filter(attendance -> employeeId == null || Objects.equals(attendance.getEmployeeId(), employeeId))
                .filter(attendance -> date == null || attendance.getWorkDate().equals(date))
                .map(attendanceMapper::toResponse)
                .toList();
    }
}

package com.hackaton.rh.leave.service.reader;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveReaderService implements LeaveReader {

    private final LeaveRequestRepository leaveRepository;
    private final LeaveMapper leaveMapper;
    private final EmployeeNameDirectory nameDirectory;

    @Override
    public LeaveResponse findById(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));
        return leaveMapper.toResponse(leave, nameDirectory.nameOf(leave.getEmployeeId()));
    }

    @Override
    public List<LeaveResponse> search(Long employeeId, LeaveStatus status, LocalDate activeOn) {
        Map<Long, String> names = nameDirectory.namesById();
        return leaveRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(leave -> employeeId == null || Objects.equals(leave.getEmployeeId(), employeeId))
                .filter(leave -> status == null || leave.getStatus() == status)
                .filter(leave -> activeOn == null || isActiveOn(leave, activeOn))
                .map(leave -> leaveMapper.toResponse(leave, names.get(leave.getEmployeeId())))
                .toList();
    }

    private boolean isActiveOn(LeaveRequest leave, LocalDate day) {
        return !leave.getStartDate().isAfter(day) && !leave.getEndDate().isBefore(day);
    }
}

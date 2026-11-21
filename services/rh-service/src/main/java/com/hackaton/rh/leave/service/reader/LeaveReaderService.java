package com.hackaton.rh.leave.service.reader;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.util.List;
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

    @Override
    public LeaveResponse findById(Long id) {
        return leaveRepository.findById(id)
                .map(leaveMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));
    }

    @Override
    public List<LeaveResponse> search(Long employeeId, LeaveStatus status) {
        return leaveRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(leave -> employeeId == null || Objects.equals(leave.getEmployeeId(), employeeId))
                .filter(leave -> status == null || leave.getStatus() == status)
                .map(leaveMapper::toResponse)
                .toList();
    }
}

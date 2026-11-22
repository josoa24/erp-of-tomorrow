package com.hackaton.rh.leave.service.decision;

import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveRejecterService implements LeaveRejecter {

    private final LeaveRequestRepository leaveRepository;
    private final LeaveMapper leaveMapper;
    private final EmployeeNameDirectory nameDirectory;

    @Override
    @Transactional
    public LeaveResponse reject(Long id, Long actorId) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));
        if (leave.getStatus() != LeaveStatus.DRAFT) {
            throw new ImmutableResourceException("Le congé " + id + " est déjà traité et ne peut plus changer");
        }
        leave.setStatus(LeaveStatus.REJECTED);
        leave.setDecidedAt(Instant.now());
        leave.setDecidedBy(actorId);
        return leaveMapper.toResponse(leaveRepository.save(leave), nameDirectory.nameOf(leave.getEmployeeId()));
    }
}

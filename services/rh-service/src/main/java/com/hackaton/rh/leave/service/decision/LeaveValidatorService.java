package com.hackaton.rh.leave.service.decision;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
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
public class LeaveValidatorService implements LeaveValidator {

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveMapper leaveMapper;

    @Override
    @Transactional
    public LeaveResponse validate(Long id) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Congé", id));
        if (leave.getStatus() != LeaveStatus.DRAFT) {
            throw new ImmutableResourceException("Le congé " + id + " est déjà traité et ne peut plus changer");
        }
        Employee employee = employeeRepository.findById(leave.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé", leave.getEmployeeId()));
        if (leave.getDays() > employee.getLeaveBalance()) {
            throw new BusinessException("Solde de congés insuffisant pour valider cette demande");
        }
        employee.setLeaveBalance(employee.getLeaveBalance() - leave.getDays());
        employeeRepository.save(employee);
        leave.setStatus(LeaveStatus.VALIDATED);
        leave.setDecidedAt(Instant.now());
        return leaveMapper.toResponse(leaveRepository.save(leave));
    }
}

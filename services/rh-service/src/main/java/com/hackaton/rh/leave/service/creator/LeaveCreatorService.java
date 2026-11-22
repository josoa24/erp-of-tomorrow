package com.hackaton.rh.leave.service.creator;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.dto.LeaveCreateRequest;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveCreatorService implements LeaveCreator {

    private static final List<LeaveStatus> BLOCKING = List.of(LeaveStatus.DRAFT, LeaveStatus.VALIDATED);

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveMapper leaveMapper;

    @Override
    @Transactional
    public LeaveResponse create(LeaveCreateRequest request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé", request.employeeId()));
        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessException("La date de fin doit être après la date de début");
        }
        int days = (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
        if (days > employee.getLeaveBalance()) {
            throw new BusinessException(
                    "Solde de congés insuffisant : " + employee.getLeaveBalance() + " jour(s) restant(s), " + days + " demandé(s)");
        }
        if (leaveRepository.existsOverlap(employee.getId(), BLOCKING, request.startDate(), request.endDate())) {
            throw new BusinessException("Une demande de congé existe déjà sur cette période");
        }
        LeaveRequest leave = new LeaveRequest();
        leave.setEmployeeId(employee.getId());
        leave.setStartDate(request.startDate());
        leave.setEndDate(request.endDate());
        leave.setDays(days);
        leave.setReason(request.reason());
        return leaveMapper.toResponse(leaveRepository.save(leave), employee.fullName());
    }
}

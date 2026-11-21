package com.hackaton.rh.summary.service;

import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import com.hackaton.rh.summary.dto.HrSummaryResponse;
import java.time.LocalDate;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrSummaryReaderService implements HrSummaryReader {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRepository;

    @Override
    public HrSummaryResponse summarize() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        return new HrSummaryResponse(
                employeeRepository.count(),
                leaveRepository.countActiveOn(LeaveStatus.VALIDATED, today),
                departmentRepository.headcountByDepartment());
    }
}

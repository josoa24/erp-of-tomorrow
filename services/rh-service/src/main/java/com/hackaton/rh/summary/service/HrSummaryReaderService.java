package com.hackaton.rh.summary.service;

import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import com.hackaton.rh.summary.dto.DepartmentHeadcount;
import com.hackaton.rh.summary.dto.HrSummaryResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
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
    private final Clock clock;

    @Override
    public HrSummaryResponse summarize() {
        LocalDate today = LocalDate.now(clock);
        List<DepartmentHeadcount> headcounts = departmentRepository.headcountByDepartment();
        return new HrSummaryResponse(
                employeeRepository.count(),
                leaveRepository.countActiveOn(LeaveStatus.VALIDATED, today),
                headcounts,
                leaveRepository.countByStatus(LeaveStatus.DRAFT),
                headcounts.stream().filter(headcount -> headcount.employeeCount() > 0).count());
    }
}

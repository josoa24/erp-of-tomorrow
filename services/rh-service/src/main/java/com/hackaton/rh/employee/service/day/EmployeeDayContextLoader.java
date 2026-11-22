package com.hackaton.rh.employee.service.day;

import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.department.entity.Department;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeDayContextLoader {

    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRepository;
    private final SalaryAdvanceRepository advanceRepository;
    private final Clock clock;

    public EmployeeDayContext load() {
        LocalDate today = LocalDate.now(clock);
        List<SalaryAdvance> advances =
                advanceRepository.findAllByMonthAndStatusIn(today.withDayOfMonth(1), AdvanceStatus.COMMITTED);
        return new EmployeeDayContext(
                departmentRepository.findAll().stream()
                        .collect(Collectors.toMap(Department::getId, Department::getName)),
                Set.copyOf(leaveRepository.findEmployeeIdsActiveOn(LeaveStatus.VALIDATED, today)),
                advances.stream().collect(Collectors.groupingBy(SalaryAdvance::getEmployeeId,
                        Collectors.reducing(BigDecimal.ZERO, SalaryAdvance::getAmount, BigDecimal::add))),
                advances.stream()
                        .filter(advance -> advance.getStatus() == AdvanceStatus.DRAFT)
                        .map(SalaryAdvance::getEmployeeId)
                        .collect(Collectors.toSet()));
    }
}

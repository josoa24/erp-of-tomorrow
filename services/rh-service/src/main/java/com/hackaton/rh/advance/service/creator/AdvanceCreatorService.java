package com.hackaton.rh.advance.service.creator;

import com.hackaton.rh.advance.dto.AdvanceCreateRequest;
import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.mapper.AdvanceMapper;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.advance.service.cap.AdvanceCapPolicy;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdvanceCreatorService implements AdvanceCreator {

    private final SalaryAdvanceRepository advanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AdvanceCapPolicy capPolicy;
    private final AdvanceMapper advanceMapper;
    private final Clock clock;

    @Override
    @Transactional
    public AdvanceResponse create(AdvanceCreateRequest request) {
        Employee employee = employeeRepository.findLockedById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé", request.employeeId()));
        LocalDate month = YearMonth.now(clock).atDay(1);
        BigDecimal committed = advanceRepository.sumAmount(employee.getId(), month, AdvanceStatus.COMMITTED);
        capPolicy.check(employee, committed, request.amount());
        SalaryAdvance advance = new SalaryAdvance();
        advance.setEmployeeId(employee.getId());
        advance.setMonth(month);
        advance.setAmount(request.amount());
        advance.setReason(request.reason());
        return advanceMapper.toResponse(advanceRepository.save(advance), employee.fullName());
    }
}

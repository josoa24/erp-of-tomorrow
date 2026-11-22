package com.hackaton.rh.advance.service.decision;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.mapper.AdvanceMapper;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.advance.service.cap.AdvanceCapPolicy;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.common.security.Actor;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdvanceValidatorService implements AdvanceValidator {

    private final AdvanceDecisionGuard decisionGuard;
    private final SalaryAdvanceRepository advanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AdvanceCapPolicy capPolicy;
    private final AdvanceMapper advanceMapper;
    private final Clock clock;

    @Override
    @Transactional
    public AdvanceResponse validate(Long id, Actor actor) {
        SalaryAdvance advance = decisionGuard.draftToDecide(id, actor);
        Employee employee = employeeRepository.findLockedById(advance.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé", advance.getEmployeeId()));
        BigDecimal validated = advanceRepository.sumAmount(
                employee.getId(), advance.getMonth(), List.of(AdvanceStatus.VALIDATED));
        capPolicy.check(employee, validated, advance.getAmount());
        advance.setStatus(AdvanceStatus.VALIDATED);
        advance.setDecidedAt(clock.instant());
        advance.setDecidedBy(actor.id());
        return advanceMapper.toResponse(advanceRepository.save(advance), employee.fullName());
    }
}

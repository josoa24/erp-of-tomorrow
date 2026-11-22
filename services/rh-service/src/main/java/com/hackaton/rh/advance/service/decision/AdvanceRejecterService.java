package com.hackaton.rh.advance.service.decision;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.mapper.AdvanceMapper;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.common.security.Actor;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdvanceRejecterService implements AdvanceRejecter {

    private final AdvanceDecisionGuard decisionGuard;
    private final SalaryAdvanceRepository advanceRepository;
    private final EmployeeNameDirectory nameDirectory;
    private final AdvanceMapper advanceMapper;
    private final Clock clock;

    @Override
    @Transactional
    public AdvanceResponse reject(Long id, Actor actor) {
        SalaryAdvance advance = decisionGuard.draftToDecide(id, actor);
        advance.setStatus(AdvanceStatus.REJECTED);
        advance.setDecidedAt(clock.instant());
        advance.setDecidedBy(actor.id());
        return advanceMapper.toResponse(advanceRepository.save(advance), nameDirectory.nameOf(advance.getEmployeeId()));
    }
}

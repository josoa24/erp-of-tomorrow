package com.hackaton.rh.advance.service.decision;

import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.common.exception.ForbiddenException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.common.security.Actor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdvanceDecisionGuard {

    private final SalaryAdvanceRepository advanceRepository;

    public SalaryAdvance draftToDecide(Long id, Actor actor) {
        if (!actor.isAdmin()) {
            throw new ForbiddenException("Seul le gérant (ADMIN) peut valider ou refuser une avance sur salaire");
        }
        SalaryAdvance advance = advanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avance", id));
        if (advance.getStatus() != AdvanceStatus.DRAFT) {
            throw new ImmutableResourceException("L'avance " + id + " est déjà traitée et ne peut plus changer");
        }
        return advance;
    }
}

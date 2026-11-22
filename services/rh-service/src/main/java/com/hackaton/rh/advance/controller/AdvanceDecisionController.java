package com.hackaton.rh.advance.controller;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.service.decision.AdvanceRejecter;
import com.hackaton.rh.advance.service.decision.AdvanceValidator;
import com.hackaton.rh.common.security.Actor;
import com.hackaton.rh.common.security.GatewayIdentityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advances")
@RequiredArgsConstructor
public class AdvanceDecisionController {

    private final AdvanceValidator advanceValidator;
    private final AdvanceRejecter advanceRejecter;

    @PostMapping("/{id}/validate")
    public AdvanceResponse validate(
            @PathVariable Long id,
            @RequestHeader(GatewayIdentityFilter.USER_ID) Long actorId,
            @RequestHeader(GatewayIdentityFilter.USER_ROLE) String role) {
        return advanceValidator.validate(id, new Actor(actorId, role));
    }

    @PostMapping("/{id}/reject")
    public AdvanceResponse reject(
            @PathVariable Long id,
            @RequestHeader(GatewayIdentityFilter.USER_ID) Long actorId,
            @RequestHeader(GatewayIdentityFilter.USER_ROLE) String role) {
        return advanceRejecter.reject(id, new Actor(actorId, role));
    }
}

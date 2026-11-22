package com.hackaton.rh.leave.controller;

import com.hackaton.rh.common.security.GatewayIdentityFilter;
import com.hackaton.rh.leave.dto.LeaveCreateRequest;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.service.creator.LeaveCreator;
import com.hackaton.rh.leave.service.decision.LeaveRejecter;
import com.hackaton.rh.leave.service.decision.LeaveValidator;
import com.hackaton.rh.leave.service.reader.LeaveReader;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveReader leaveReader;
    private final LeaveCreator leaveCreator;
    private final LeaveValidator leaveValidator;
    private final LeaveRejecter leaveRejecter;

    @GetMapping
    public List<LeaveResponse> search(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) LocalDate activeOn) {
        return leaveReader.search(employeeId, status, activeOn);
    }

    @GetMapping("/{id}")
    public LeaveResponse findById(@PathVariable Long id) {
        return leaveReader.findById(id);
    }

    @PostMapping
    public ResponseEntity<LeaveResponse> create(@Valid @RequestBody LeaveCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveCreator.create(request));
    }

    @PostMapping("/{id}/validate")
    public LeaveResponse validate(@PathVariable Long id, @RequestHeader(GatewayIdentityFilter.USER_ID) Long actorId) {
        return leaveValidator.validate(id, actorId);
    }

    @PostMapping("/{id}/reject")
    public LeaveResponse reject(@PathVariable Long id, @RequestHeader(GatewayIdentityFilter.USER_ID) Long actorId) {
        return leaveRejecter.reject(id, actorId);
    }
}

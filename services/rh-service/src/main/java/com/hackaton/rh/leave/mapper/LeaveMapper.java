package com.hackaton.rh.leave.mapper;

import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {

    public LeaveResponse toResponse(LeaveRequest leave, String employeeName) {
        return new LeaveResponse(
                leave.getId(),
                leave.getEmployeeId(),
                employeeName,
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getDays(),
                leave.getReason(),
                leave.getStatus(),
                leave.getCreatedAt(),
                leave.getDecidedAt(),
                leave.getDecidedBy());
    }
}

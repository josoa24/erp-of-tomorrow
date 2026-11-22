package com.hackaton.rh.advance.mapper;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

@Component
public class AdvanceMapper {

    public AdvanceResponse toResponse(SalaryAdvance advance, String employeeName) {
        return new AdvanceResponse(
                advance.getId(),
                advance.getEmployeeId(),
                employeeName,
                YearMonth.from(advance.getMonth()),
                advance.getAmount(),
                advance.getReason(),
                advance.getStatus(),
                advance.getCreatedAt(),
                advance.getDecidedAt(),
                advance.getDecidedBy());
    }
}

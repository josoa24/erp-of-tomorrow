package com.hackaton.rh.leave.service.decision;

import com.hackaton.rh.leave.dto.LeaveResponse;

public interface LeaveValidator {

    LeaveResponse validate(Long id);
}

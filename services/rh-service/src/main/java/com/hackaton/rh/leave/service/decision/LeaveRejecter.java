package com.hackaton.rh.leave.service.decision;

import com.hackaton.rh.leave.dto.LeaveResponse;

public interface LeaveRejecter {

    LeaveResponse reject(Long id, Long actorId);
}

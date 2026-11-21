package com.hackaton.rh.leave.service.creator;

import com.hackaton.rh.leave.dto.LeaveCreateRequest;
import com.hackaton.rh.leave.dto.LeaveResponse;

public interface LeaveCreator {

    LeaveResponse create(LeaveCreateRequest request);
}

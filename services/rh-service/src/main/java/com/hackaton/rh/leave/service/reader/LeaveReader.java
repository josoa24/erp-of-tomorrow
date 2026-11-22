package com.hackaton.rh.leave.service.reader;

import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveStatus;
import java.time.LocalDate;
import java.util.List;

public interface LeaveReader {

    LeaveResponse findById(Long id);

    List<LeaveResponse> search(Long employeeId, LeaveStatus status, LocalDate activeOn);
}

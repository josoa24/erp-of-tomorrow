package com.hackaton.rh.advance.service.reader;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import java.time.YearMonth;
import java.util.List;

public interface AdvanceReader {

    List<AdvanceResponse> search(YearMonth month, Long employeeId, AdvanceStatus status);
}

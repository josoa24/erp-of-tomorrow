package com.hackaton.rh.employee.service.day;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public record EmployeeDayContext(
        Map<Long, String> departmentNames,
        Set<Long> onLeave,
        Map<Long, BigDecimal> monthAdvances,
        Set<Long> pendingAdvances) {
}

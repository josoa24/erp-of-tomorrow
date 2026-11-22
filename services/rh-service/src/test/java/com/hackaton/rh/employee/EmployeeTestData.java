package com.hackaton.rh.employee;

import com.hackaton.rh.advance.service.cap.AdvanceCapPolicy;
import com.hackaton.rh.employee.mapper.EmployeeMapper;
import com.hackaton.rh.employee.service.day.EmployeeDayContext;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public final class EmployeeTestData {

    public static final EmployeeDayContext EMPTY_DAY = new EmployeeDayContext(Map.of(), Set.of(), Map.of(), Set.of());

    private EmployeeTestData() {
    }

    public static EmployeeMapper mapper() {
        return new EmployeeMapper(new AdvanceCapPolicy(new BigDecimal("30")));
    }
}

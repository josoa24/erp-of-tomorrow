package com.hackaton.rh.employee.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.rh.employee.EmployeeTestData;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.dto.EmployeeStatus;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.service.day.EmployeeDayContext;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = EmployeeTestData.mapper();

    @Test
    void shouldExposeMonthAdvanceAndCap_whenEmployeeHasPendingAdvance() {
        EmployeeDayContext day = new EmployeeDayContext(Map.of(3L, "Stock"), Set.of(),
                Map.of(8L, new BigDecimal("80000")), Set.of(8L));

        EmployeeResponse response = mapper.toResponse(nirina(), day);

        assertEquals("Stock", response.departmentName());
        assertEquals(new BigDecimal("80000"), response.monthAdvance());
        assertTrue(response.monthAdvancePending());
        assertEquals(new BigDecimal("210000.00"), response.advanceCap());
        assertEquals(EmployeeStatus.PRESENT, response.status());
    }

    @Test
    void shouldReportZeroAdvanceAndLeave_whenEmployeeIsOnLeaveWithoutAdvance() {
        EmployeeDayContext day = new EmployeeDayContext(Map.of(), Set.of(8L), Map.of(), Set.of());

        EmployeeResponse response = mapper.toResponse(nirina(), day);

        assertTrue(response.onLeaveToday());
        assertEquals(EmployeeStatus.ON_LEAVE, response.status());
        assertEquals(BigDecimal.ZERO, response.monthAdvance());
        assertFalse(response.monthAdvancePending());
    }

    private Employee nirina() {
        Employee employee = new Employee();
        employee.setId(8L);
        employee.setFirstName("Nirina");
        employee.setLastName("Rabe");
        employee.setDepartmentId(3L);
        employee.setBaseSalary(new BigDecimal("700000"));
        return employee;
    }
}

package com.hackaton.rh.employee.service.counts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.rh.employee.dto.EmployeeCountsResponse;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.employee.service.day.EmployeeDayContext;
import com.hackaton.rh.employee.service.day.EmployeeDayContextLoader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeCountReaderServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeDayContextLoader dayContextLoader;

    @InjectMocks
    private EmployeeCountReaderService reader;

    @Test
    void shouldSplitPresentAndOnLeave_andCountPendingAdvances() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee(7L), employee(8L), employee(9L)));
        when(dayContextLoader.load()).thenReturn(new EmployeeDayContext(Map.of(), Set.of(8L),
                Map.of(7L, new BigDecimal("150000"), 8L, new BigDecimal("80000")), Set.of(8L)));

        EmployeeCountsResponse counts = reader.count();

        assertEquals(new EmployeeCountsResponse(3, 2, 1, 1), counts);
    }

    private Employee employee(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }
}

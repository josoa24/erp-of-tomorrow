package com.hackaton.rh.employee.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.EmployeeTestData;
import com.hackaton.rh.employee.dto.EmployeeStatus;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.employee.service.day.EmployeeDayContext;
import com.hackaton.rh.employee.service.day.EmployeeDayContextLoader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeReaderServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeDayContextLoader dayContextLoader;

    private EmployeeReaderService reader;

    @BeforeEach
    void setUp() {
        reader = new EmployeeReaderService(employeeRepository, EmployeeTestData.mapper(), dayContextLoader);
    }

    @Test
    void shouldFilterEmployeesBySearchTermAndDepartment() {
        when(dayContextLoader.load()).thenReturn(EmployeeTestData.EMPTY_DAY);
        when(employeeRepository.findAllByOrderByLastNameAscFirstNameAsc())
                .thenReturn(List.of(employee(1L, "Aina", "Rakoto", 1L), employee(2L, "Hery", "Rabe", 2L)));

        assertEquals(2, reader.search(null, null).size());
        assertEquals(1, reader.search("rakoto", null).size());
        assertEquals(1, reader.search(null, 2L).size());
        assertEquals(0, reader.search("rakoto", 2L).size());
    }

    @Test
    void shouldDescribeTodayStatus_whenReadingOneEmployee() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee(2L, "Hery", "Rabe", 2L)));
        when(dayContextLoader.load()).thenReturn(
                new EmployeeDayContext(Map.of(2L, "Logistique"), Set.of(2L), Map.of(), Set.of()));

        assertEquals(EmployeeStatus.ON_LEAVE, reader.findById(2L).status());
        assertEquals("Logistique", reader.findById(2L).departmentName());
    }

    @Test
    void shouldThrowNotFound_whenEmployeeDoesNotExist() {
        when(employeeRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reader.findById(9L));
    }

    private Employee employee(Long id, String first, String last, Long departmentId) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(first);
        employee.setLastName(last);
        employee.setEmail(first.toLowerCase() + "@erp.local");
        employee.setDepartmentId(departmentId);
        employee.setHireDate(LocalDate.of(2024, 1, 1));
        employee.setBaseSalary(BigDecimal.TEN);
        return employee;
    }
}

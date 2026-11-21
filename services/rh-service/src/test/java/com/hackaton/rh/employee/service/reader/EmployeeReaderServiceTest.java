package com.hackaton.rh.employee.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.mapper.EmployeeMapper;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeReaderServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void shouldFilterEmployeesBySearchTermAndDepartment() {
        when(employeeRepository.findAllByOrderByLastNameAscFirstNameAsc())
                .thenReturn(List.of(employee("Aina", "Rakoto", 1L), employee("Hery", "Rabe", 2L)));
        EmployeeReaderService reader = new EmployeeReaderService(employeeRepository, new EmployeeMapper());

        assertEquals(2, reader.search(null, null).size());
        assertEquals(1, reader.search("rakoto", null).size());
        assertEquals(1, reader.search(null, 2L).size());
        assertEquals(0, reader.search("rakoto", 2L).size());
    }

    private Employee employee(String first, String last, Long departmentId) {
        Employee employee = new Employee();
        employee.setFirstName(first);
        employee.setLastName(last);
        employee.setEmail(first.toLowerCase() + "@erp.local");
        employee.setDepartmentId(departmentId);
        employee.setHireDate(LocalDate.of(2024, 1, 1));
        employee.setBaseSalary(BigDecimal.TEN);
        return employee;
    }
}

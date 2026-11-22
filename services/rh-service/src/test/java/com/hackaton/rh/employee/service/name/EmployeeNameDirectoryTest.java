package com.hackaton.rh.employee.service.name;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeNameDirectoryTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeNameDirectory directory;

    @Test
    void shouldMapEveryEmployeeToFullName_whenListingNames() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee(7L, "Hery", "Rabe")));

        assertEquals(Map.of(7L, "Hery Rabe"), directory.namesById());
    }

    @Test
    void shouldReturnNull_whenEmployeeNoLongerExists() {
        when(employeeRepository.findById(9L)).thenReturn(Optional.empty());

        assertNull(directory.nameOf(9L));
    }

    private Employee employee(Long id, String firstName, String lastName) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        return employee;
    }
}

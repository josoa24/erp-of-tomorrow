package com.hackaton.rh.employee.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.entity.ContractType;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.mapper.EmployeeMapper;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.employee.service.link.EmployeeAccountLinkChecker;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeCreatorServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    private EmployeeCreatorService service;
    private final EmployeeRequest request = new EmployeeRequest(
            "Aina", "Rakoto", "Aina.Rakoto@erp.local", "Comptable", 1L, LocalDate.of(2024, 1, 15),
            new BigDecimal("900000"), null, ContractType.CDD);

    @BeforeEach
    void setUp() {
        service = new EmployeeCreatorService(employeeRepository, departmentRepository, new EmployeeMapper(),
                new EmployeeAccountLinkChecker(employeeRepository));
    }

    @Test
    void shouldCreateEmployeeWithInitialLeaveBalanceAndLowercaseEmail() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.existsByEmail("aina.rakoto@erp.local")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(call -> call.getArgument(0));

        EmployeeResponse response = service.create(request);

        assertEquals(Employee.INITIAL_LEAVE_BALANCE, response.leaveBalance());
        assertEquals("aina.rakoto@erp.local", response.email());
        assertEquals(ContractType.CDD, response.contractType());
    }

    @Test
    void shouldRefuseEmployee_whenDepartmentDoesNotExist() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void shouldRefuseEmployee_whenEmailAlreadyUsed() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.existsByEmail("aina.rakoto@erp.local")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request));
        verify(employeeRepository, never()).save(any());
    }
}

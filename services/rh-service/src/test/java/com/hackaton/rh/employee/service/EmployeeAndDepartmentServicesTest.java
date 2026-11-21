package com.hackaton.rh.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.attendance.repository.AttendanceRepository;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.department.service.deleter.DepartmentDeleterService;
import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.mapper.EmployeeMapper;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.employee.service.creator.EmployeeCreatorService;
import com.hackaton.rh.employee.service.deleter.EmployeeDeleterService;
import com.hackaton.rh.employee.service.reader.EmployeeReaderService;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeAndDepartmentServicesTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private AttendanceRepository attendanceRepository;

    private final EmployeeMapper mapper = new EmployeeMapper();
    private final EmployeeRequest request = new EmployeeRequest(
            "Aina", "Rakoto", "Aina.Rakoto@erp.local", "Comptable", 1L, LocalDate.of(2024, 1, 15), new BigDecimal("900000"));

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

    @Test
    void shouldCreateEmployeeWithInitialLeaveBalanceAndLowercaseEmail() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.existsByEmail("aina.rakoto@erp.local")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(call -> call.getArgument(0));

        EmployeeResponse response =
                new EmployeeCreatorService(employeeRepository, departmentRepository, mapper).create(request);

        assertEquals(Employee.INITIAL_LEAVE_BALANCE, response.leaveBalance());
        assertEquals("aina.rakoto@erp.local", response.email());
    }

    @Test
    void shouldRefuseEmployee_whenDepartmentDoesNotExist() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> new EmployeeCreatorService(employeeRepository, departmentRepository, mapper).create(request));
    }

    @Test
    void shouldRefuseEmployee_whenEmailAlreadyUsed() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.existsByEmail("aina.rakoto@erp.local")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> new EmployeeCreatorService(employeeRepository, departmentRepository, mapper).create(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldFilterEmployeesBySearchTermAndDepartment() {
        when(employeeRepository.findAllByOrderByLastNameAscFirstNameAsc())
                .thenReturn(List.of(employee("Aina", "Rakoto", 1L), employee("Hery", "Rabe", 2L)));
        EmployeeReaderService reader = new EmployeeReaderService(employeeRepository, mapper);

        assertEquals(2, reader.search(null, null).size());
        assertEquals(1, reader.search("rakoto", null).size());
        assertEquals(1, reader.search(null, 2L).size());
        assertEquals(0, reader.search("rakoto", 2L).size());
    }

    @Test
    void shouldRefuseDepartmentDeletion_whenItHasEmployees() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.existsByDepartmentId(1L)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> new DepartmentDeleterService(departmentRepository, employeeRepository).delete(1L));
        verify(departmentRepository, never()).deleteById(any());
    }

    @Test
    void shouldRefuseEmployeeDeletion_whenHistoryExists() {
        when(employeeRepository.existsById(7L)).thenReturn(true);
        when(leaveRepository.existsByEmployeeId(7L)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> new EmployeeDeleterService(employeeRepository, leaveRepository, attendanceRepository).delete(7L));
        verify(employeeRepository, never()).deleteById(any());
    }
}

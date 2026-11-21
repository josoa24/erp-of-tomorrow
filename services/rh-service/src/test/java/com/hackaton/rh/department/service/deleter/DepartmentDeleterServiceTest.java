package com.hackaton.rh.department.service.deleter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepartmentDeleterServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentDeleterService service;

    @Test
    void shouldRefuseDepartmentDeletion_whenItHasEmployees() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.existsByDepartmentId(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.delete(1L));
        verify(departmentRepository, never()).deleteById(any());
    }
}

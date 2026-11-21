package com.hackaton.rh.department.service.deleter;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentDeleterService implements DepartmentDeleter {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Département", id);
        }
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new BusinessException("Ce département a des employés et ne peut pas être supprimé");
        }
        departmentRepository.deleteById(id);
    }
}

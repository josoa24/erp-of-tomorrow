package com.hackaton.rh.employee.service.creator;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.mapper.EmployeeMapper;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeCreatorService implements EmployeeCreator {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (!departmentRepository.existsById(request.departmentId())) {
            throw new ResourceNotFoundException("Département", request.departmentId());
        }
        String email = request.email().trim().toLowerCase();
        if (employeeRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Un employé avec l'email " + email + " existe déjà");
        }
        return employeeMapper.toResponse(employeeRepository.save(employeeMapper.toEntity(request)));
    }
}

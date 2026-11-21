package com.hackaton.rh.employee.service.updater;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.repository.DepartmentRepository;
import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.mapper.EmployeeMapper;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.employee.service.link.EmployeeAccountLinkChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeUpdaterService implements EmployeeUpdater {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeAccountLinkChecker accountLinkChecker;

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id));
        if (!departmentRepository.existsById(request.departmentId())) {
            throw new ResourceNotFoundException("Département", request.departmentId());
        }
        String email = request.email().trim().toLowerCase();
        if (employeeRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Un employé avec l'email " + email + " existe déjà");
        }
        accountLinkChecker.checkAvailable(request.userId(), id);
        employeeMapper.apply(employee, request);
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }
}

package com.hackaton.rh.employee.service.reader;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.mapper.EmployeeMapper;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeReaderService implements EmployeeReader {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponse findById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id));
    }

    @Override
    public List<EmployeeResponse> search(String search, Long departmentId) {
        String term = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return employeeRepository.findAllByOrderByLastNameAscFirstNameAsc().stream()
                .filter(employee -> departmentId == null || Objects.equals(employee.getDepartmentId(), departmentId))
                .filter(employee -> matches(employee, term))
                .map(employeeMapper::toResponse)
                .toList();
    }

    private boolean matches(Employee employee, String term) {
        if (term.isEmpty()) {
            return true;
        }
        String haystack = (employee.getFirstName() + " " + employee.getLastName() + " " + employee.getEmail())
                .toLowerCase(Locale.ROOT);
        return haystack.contains(term);
    }
}

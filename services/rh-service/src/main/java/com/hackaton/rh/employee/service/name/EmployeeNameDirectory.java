package com.hackaton.rh.employee.service.name;

import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeNameDirectory {

    private final EmployeeRepository employeeRepository;

    public Map<Long, String> namesById() {
        return employeeRepository.findAll().stream()
                .collect(Collectors.toMap(Employee::getId, Employee::fullName));
    }

    public String nameOf(Long employeeId) {
        return employeeRepository.findById(employeeId).map(Employee::fullName).orElse(null);
    }
}

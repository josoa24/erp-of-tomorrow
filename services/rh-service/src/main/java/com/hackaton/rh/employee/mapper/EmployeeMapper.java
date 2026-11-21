package com.hackaton.rh.employee.mapper;

import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        apply(employee, request);
        return employee;
    }

    public void apply(Employee employee, EmployeeRequest request) {
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email().trim().toLowerCase());
        employee.setPosition(request.position());
        employee.setDepartmentId(request.departmentId());
        employee.setHireDate(request.hireDate());
        employee.setBaseSalary(request.baseSalary());
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPosition(),
                employee.getDepartmentId(),
                employee.getHireDate(),
                employee.getBaseSalary(),
                employee.getLeaveBalance(),
                employee.getCreatedAt());
    }
}

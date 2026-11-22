package com.hackaton.rh.employee.mapper;

import com.hackaton.rh.advance.service.cap.AdvanceCapPolicy;
import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.dto.EmployeeStatus;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.service.day.EmployeeDayContext;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final AdvanceCapPolicy capPolicy;

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
        employee.setContractType(request.contractType());
        employee.setDepartmentId(request.departmentId());
        employee.setHireDate(request.hireDate());
        employee.setBaseSalary(request.baseSalary());
        employee.setUserId(request.userId());
    }

    public EmployeeResponse toResponse(Employee employee, EmployeeDayContext day) {
        boolean onLeave = day.onLeave().contains(employee.getId());
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPosition(),
                employee.getContractType(),
                employee.getDepartmentId(),
                day.departmentNames().get(employee.getDepartmentId()),
                employee.getHireDate(),
                employee.getBaseSalary(),
                employee.getLeaveBalance(),
                employee.getUserId(),
                employee.getCreatedAt(),
                onLeave,
                onLeave ? EmployeeStatus.ON_LEAVE : EmployeeStatus.PRESENT,
                day.monthAdvances().getOrDefault(employee.getId(), BigDecimal.ZERO),
                day.pendingAdvances().contains(employee.getId()),
                capPolicy.capOf(employee.getBaseSalary()));
    }
}

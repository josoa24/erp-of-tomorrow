package com.hackaton.rh.department.mapper;

import com.hackaton.rh.department.dto.DepartmentRequest;
import com.hackaton.rh.department.dto.DepartmentResponse;
import com.hackaton.rh.department.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequest request) {
        Department department = new Department();
        department.setName(request.name().trim());
        return department;
    }

    public DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(department.getId(), department.getName(), department.getCreatedAt());
    }
}

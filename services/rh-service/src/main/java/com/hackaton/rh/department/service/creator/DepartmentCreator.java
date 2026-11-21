package com.hackaton.rh.department.service.creator;

import com.hackaton.rh.department.dto.DepartmentRequest;
import com.hackaton.rh.department.dto.DepartmentResponse;

public interface DepartmentCreator {

    DepartmentResponse create(DepartmentRequest request);
}

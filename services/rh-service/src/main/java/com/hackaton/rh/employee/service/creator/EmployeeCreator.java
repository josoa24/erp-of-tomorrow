package com.hackaton.rh.employee.service.creator;

import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;

public interface EmployeeCreator {

    EmployeeResponse create(EmployeeRequest request);
}

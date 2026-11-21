package com.hackaton.rh.employee.service.updater;

import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;

public interface EmployeeUpdater {

    EmployeeResponse update(Long id, EmployeeRequest request);
}

package com.hackaton.rh.department.service.updater;

import com.hackaton.rh.department.dto.DepartmentRequest;
import com.hackaton.rh.department.dto.DepartmentResponse;

public interface DepartmentUpdater {

    DepartmentResponse update(Long id, DepartmentRequest request);
}

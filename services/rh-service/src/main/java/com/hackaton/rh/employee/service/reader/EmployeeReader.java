package com.hackaton.rh.employee.service.reader;

import com.hackaton.rh.employee.dto.EmployeeResponse;
import java.util.List;

public interface EmployeeReader {

    EmployeeResponse findById(Long id);

    List<EmployeeResponse> search(String search, Long departmentId);
}

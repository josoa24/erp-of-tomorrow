package com.hackaton.rh.department.service.reader;

import com.hackaton.rh.department.dto.DepartmentResponse;
import java.util.List;

public interface DepartmentReader {

    DepartmentResponse findById(Long id);

    List<DepartmentResponse> findAll();
}

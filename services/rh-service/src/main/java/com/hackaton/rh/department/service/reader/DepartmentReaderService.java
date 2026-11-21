package com.hackaton.rh.department.service.reader;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.dto.DepartmentResponse;
import com.hackaton.rh.department.mapper.DepartmentMapper;
import com.hackaton.rh.department.repository.DepartmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentReaderService implements DepartmentReader {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public DepartmentResponse findById(Long id) {
        return departmentRepository.findById(id)
                .map(departmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));
    }

    @Override
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll().stream().map(departmentMapper::toResponse).toList();
    }
}

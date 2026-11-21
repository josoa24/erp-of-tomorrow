package com.hackaton.rh.department.service.creator;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.department.dto.DepartmentRequest;
import com.hackaton.rh.department.dto.DepartmentResponse;
import com.hackaton.rh.department.mapper.DepartmentMapper;
import com.hackaton.rh.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentCreatorService implements DepartmentCreator {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new DuplicateResourceException("Le département " + request.name().trim() + " existe déjà");
        }
        return departmentMapper.toResponse(departmentRepository.save(departmentMapper.toEntity(request)));
    }
}

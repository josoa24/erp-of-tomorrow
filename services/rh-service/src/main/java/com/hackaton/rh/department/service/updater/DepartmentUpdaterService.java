package com.hackaton.rh.department.service.updater;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.dto.DepartmentRequest;
import com.hackaton.rh.department.dto.DepartmentResponse;
import com.hackaton.rh.department.entity.Department;
import com.hackaton.rh.department.mapper.DepartmentMapper;
import com.hackaton.rh.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentUpdaterService implements DepartmentUpdater {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));
        String newName = request.name().trim();
        boolean renamed = !department.getName().equalsIgnoreCase(newName);
        if (renamed && departmentRepository.existsByNameIgnoreCase(newName)) {
            throw new DuplicateResourceException("Le département " + newName + " existe déjà");
        }
        department.setName(newName);
        return departmentMapper.toResponse(departmentRepository.save(department));
    }
}

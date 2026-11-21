package com.hackaton.rh.department.controller;

import com.hackaton.rh.department.dto.DepartmentRequest;
import com.hackaton.rh.department.dto.DepartmentResponse;
import com.hackaton.rh.department.service.creator.DepartmentCreator;
import com.hackaton.rh.department.service.deleter.DepartmentDeleter;
import com.hackaton.rh.department.service.reader.DepartmentReader;
import com.hackaton.rh.department.service.updater.DepartmentUpdater;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentReader departmentReader;
    private final DepartmentCreator departmentCreator;
    private final DepartmentUpdater departmentUpdater;
    private final DepartmentDeleter departmentDeleter;

    @GetMapping
    public List<DepartmentResponse> findAll() {
        return departmentReader.findAll();
    }

    @GetMapping("/{id}")
    public DepartmentResponse findById(@PathVariable Long id) {
        return departmentReader.findById(id);
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentCreator.create(request));
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return departmentUpdater.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentDeleter.delete(id);
    }
}

package com.hackaton.rh.employee.controller;

import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.service.creator.EmployeeCreator;
import com.hackaton.rh.employee.service.deleter.EmployeeDeleter;
import com.hackaton.rh.employee.service.reader.EmployeeReader;
import com.hackaton.rh.employee.service.updater.EmployeeUpdater;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeReader employeeReader;
    private final EmployeeCreator employeeCreator;
    private final EmployeeUpdater employeeUpdater;
    private final EmployeeDeleter employeeDeleter;

    @GetMapping
    public List<EmployeeResponse> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId) {
        return employeeReader.search(search, departmentId);
    }

    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable Long id) {
        return employeeReader.findById(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeCreator.create(request));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeUpdater.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeDeleter.delete(id);
    }
}

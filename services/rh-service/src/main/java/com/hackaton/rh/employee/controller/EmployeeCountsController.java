package com.hackaton.rh.employee.controller;

import com.hackaton.rh.employee.dto.EmployeeCountsResponse;
import com.hackaton.rh.employee.service.counts.EmployeeCountReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees/counts")
@RequiredArgsConstructor
public class EmployeeCountsController {

    private final EmployeeCountReader countReader;

    @GetMapping
    public EmployeeCountsResponse count() {
        return countReader.count();
    }
}

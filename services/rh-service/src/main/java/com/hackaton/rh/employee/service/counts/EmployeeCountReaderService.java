package com.hackaton.rh.employee.service.counts;

import com.hackaton.rh.employee.dto.EmployeeCountsResponse;
import com.hackaton.rh.employee.entity.Employee;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.employee.service.day.EmployeeDayContext;
import com.hackaton.rh.employee.service.day.EmployeeDayContextLoader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeCountReaderService implements EmployeeCountReader {

    private final EmployeeRepository employeeRepository;
    private final EmployeeDayContextLoader dayContextLoader;

    @Override
    public EmployeeCountsResponse count() {
        List<Long> ids = employeeRepository.findAll().stream().map(Employee::getId).toList();
        EmployeeDayContext day = dayContextLoader.load();
        long onLeave = ids.stream().filter(day.onLeave()::contains).count();
        long advanceRequested = ids.stream().filter(day.pendingAdvances()::contains).count();
        return new EmployeeCountsResponse(ids.size(), ids.size() - onLeave, onLeave, advanceRequested);
    }
}

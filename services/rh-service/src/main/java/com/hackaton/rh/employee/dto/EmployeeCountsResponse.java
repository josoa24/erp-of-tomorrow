package com.hackaton.rh.employee.dto;

public record EmployeeCountsResponse(long all, long present, long onLeave, long advanceRequested) {
}

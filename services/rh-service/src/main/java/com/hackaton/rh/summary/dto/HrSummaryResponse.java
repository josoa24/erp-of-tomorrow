package com.hackaton.rh.summary.dto;

import java.util.List;

public record HrSummaryResponse(long totalEmployees, long onLeaveToday, List<DepartmentHeadcount> headcountByDepartment) {
}

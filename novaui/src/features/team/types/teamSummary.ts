export interface DepartmentHeadcount {
  departmentId: number
  departmentName: string
  employeeCount: number
}

export interface TeamSummary {
  totalEmployees: number
  onLeaveToday: number
  headcountByDepartment: DepartmentHeadcount[]
  pendingLeaves: number
  departmentsWithEmployees: number
}

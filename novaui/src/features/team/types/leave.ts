export type LeaveStatus = 'DRAFT' | 'VALIDATED' | 'REJECTED'

export interface Leave {
  id: number
  employeeId: number
  employeeName: string | null
  startDate: string
  endDate: string
  days: number
  reason: string | null
  status: LeaveStatus
  createdAt: string
  decidedAt: string | null
  decidedBy: number | null
}

export interface LeaveCreateRequest {
  employeeId: number
  startDate: string
  endDate: string
  reason?: string
}

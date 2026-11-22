export type AdvanceStatus = 'DRAFT' | 'VALIDATED' | 'REJECTED'

export interface Advance {
  id: number
  employeeId: number
  employeeName: string | null
  month: string
  amount: number
  reason: string | null
  status: AdvanceStatus
  createdAt: string
  decidedAt: string | null
  decidedBy: number | null
}

export interface AdvanceCreateRequest {
  employeeId: number
  amount: number
  reason?: string
}

export interface AdvanceSummary {
  monthTotal: number
  requests: number
  pending: number
}

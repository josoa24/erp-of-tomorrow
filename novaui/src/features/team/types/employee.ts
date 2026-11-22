export type ContractType = 'CDI' | 'CDD' | 'STAGE' | 'JOURNALIER'
export type EmployeeStatus = 'PRESENT' | 'ON_LEAVE'

export interface Employee {
  id: number
  firstName: string
  lastName: string
  email: string
  position: string | null
  contractType: ContractType | null
  departmentId: number
  departmentName: string | null
  hireDate: string
  baseSalary: number
  leaveBalance: number
  userId: number | null
  createdAt: string
  onLeaveToday: boolean
  status: EmployeeStatus
  monthAdvance: number
  monthAdvancePending: boolean
  advanceCap: number
}

export interface EmployeeCreateRequest {
  firstName: string
  lastName: string
  email: string
  position?: string
  departmentId: number
  hireDate: string
  baseSalary: number
  contractType?: ContractType
}

export interface EmployeeCounts {
  all: number
  present: number
  onLeave: number
  advanceRequested: number
}

export interface Department {
  id: number
  name: string
  createdAt: string
}

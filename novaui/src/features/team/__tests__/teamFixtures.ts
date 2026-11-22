import type { Employee } from '../types/employee'
import type { Leave } from '../types/leave'
import type { TeamSummary } from '../types/teamSummary'

const base: Employee = {
  id: 1,
  firstName: 'Toky',
  lastName: 'Rakoto',
  email: 'toky.rakoto@boutique-tana.mg',
  position: 'Chauffeur',
  contractType: 'CDI',
  departmentId: 1,
  departmentName: 'Logistique',
  hireDate: '2021-03-15',
  baseSalary: 800000,
  leaveBalance: 25,
  userId: null,
  createdAt: '2021-03-15T06:00:00Z',
  onLeaveToday: false,
  status: 'PRESENT',
  monthAdvance: 150000,
  monthAdvancePending: false,
  advanceCap: 240000,
}

export const anEmployee = (overrides: Partial<Employee> = {}): Employee => ({ ...base, ...overrides })

export const TEAM: Employee[] = [
  anEmployee({ id: 4, firstName: 'Fara', lastName: 'Andria', position: 'Vendeuse', departmentName: 'Ventes', monthAdvance: 0, advanceCap: 270000 }),
  anEmployee({
    id: 3,
    firstName: 'Nirina',
    lastName: 'Rabe',
    position: 'Magasinière',
    contractType: 'CDD',
    departmentName: 'Stock',
    hireDate: '2024-01-08',
    status: 'ON_LEAVE',
    onLeaveToday: true,
    monthAdvance: 80000,
    monthAdvancePending: true,
    advanceCap: 210000,
  }),
  anEmployee(),
  anEmployee({ id: 2, firstName: 'Haja', lastName: 'Randria', hireDate: '2022-06-01', monthAdvance: 0, contractType: null }),
]

export const SUMMARY: TeamSummary = {
  totalEmployees: 37,
  onLeaveToday: 3,
  headcountByDepartment: [],
  pendingLeaves: 3,
  departmentsWithEmployees: 4,
}

export const aLeave = (overrides: Partial<Leave> = {}): Leave => ({
  id: 11,
  employeeId: 3,
  employeeName: 'Nirina Rabe',
  startDate: '2026-12-23',
  endDate: '2026-12-27',
  days: 5,
  reason: 'Fêtes de fin d’année',
  status: 'DRAFT',
  createdAt: '2026-11-20T07:00:00Z',
  decidedAt: null,
  decidedBy: null,
  ...overrides,
})

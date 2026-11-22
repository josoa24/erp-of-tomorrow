import type { Employee } from '../types/employee'
import type { TeamFilter } from './teamSearch'

const collator = new Intl.Collator('fr-FR', { sensitivity: 'base' })

const MATCHERS: Record<TeamFilter, (employee: Employee) => boolean> = {
  tous: () => true,
  presents: (employee) => employee.status === 'PRESENT',
  conges: (employee) => employee.status === 'ON_LEAVE',
  avances: (employee) => employee.monthAdvancePending,
}

const compareByDepartment = (left: Employee, right: Employee): number =>
  collator.compare(left.departmentName ?? '', right.departmentName ?? '') ||
  collator.compare(left.lastName, right.lastName) ||
  collator.compare(left.firstName, right.firstName)

export const visibleEmployees = (employees: Employee[], filter: TeamFilter): Employee[] =>
  employees.filter(MATCHERS[filter]).sort(compareByDepartment)

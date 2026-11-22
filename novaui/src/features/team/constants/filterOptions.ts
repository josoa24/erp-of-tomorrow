import type { EmployeeCounts } from '../types/employee'
import type { TeamFilter } from '../services/teamSearch'

export interface TeamFilterOption {
  value: TeamFilter
  label: string
  countKey: keyof EmployeeCounts
  adaptation?: boolean
}

export const TEAM_FILTER_OPTIONS: readonly TeamFilterOption[] = [
  { value: 'tous', label: 'Tous', countKey: 'all' },
  { value: 'presents', label: 'Présents', countKey: 'present' },
  { value: 'conges', label: 'En congé', countKey: 'onLeave' },
  { value: 'avances', label: 'Avance demandée', countKey: 'advanceRequested', adaptation: true },
]

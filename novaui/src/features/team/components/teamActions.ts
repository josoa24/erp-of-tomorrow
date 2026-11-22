import { CalendarPlus, Check, Wallet } from 'lucide-react'
import type { RowAction } from '../../../components/module/types'
import type { Employee } from '../types/employee'

export interface TeamActionHandlers {
  adapted: boolean
  isAdmin: boolean
  onLeave: (employee: Employee) => void
  onAdvance: (employee: Employee) => void
  onAdvanceDecisions: (employee: Employee) => void
}

export const teamActions =
  ({ adapted, isAdmin, onLeave, onAdvance, onAdvanceDecisions }: TeamActionHandlers) =>
  (employee: Employee): RowAction[] => [
    { key: 'leave', label: 'Demander un congé', icon: CalendarPlus, onSelect: () => onLeave(employee) },
    ...(adapted ? [{ key: 'advance', label: 'Demander une avance', icon: Wallet, onSelect: () => onAdvance(employee) }] : []),
    ...(adapted && isAdmin && employee.monthAdvancePending
      ? [{ key: 'decide', label: 'Valider ou refuser l’avance', icon: Check, onSelect: () => onAdvanceDecisions(employee) }]
      : []),
  ]

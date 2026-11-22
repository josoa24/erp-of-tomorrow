import { createAdvance, decideAdvance } from '../api/advancesApi'
import { createEmployee } from '../api/employeesApi'
import { createLeave, decideLeave } from '../api/leavesApi'
import { fullName } from '../services/teamFormat'
import type { Decision } from '../types/decision'
import { useTeamMutation } from './useTeamMutation'

export interface DecisionVariables {
  id: number
  decision: Decision
}

const nameOf = (employeeName: string | null): string => employeeName ?? 'l’employé'

export const useCreateEmployee = () =>
  useTeamMutation(createEmployee, (employee) => `${fullName(employee)} a rejoint l’équipe`)

export const useRequestLeave = () =>
  useTeamMutation(createLeave, (leave) => `Demande de congé enregistrée pour ${nameOf(leave.employeeName)}`)

export const useLeaveDecision = () =>
  useTeamMutation(
    ({ id, decision }: DecisionVariables) => decideLeave(id, decision),
    (leave, { decision }) => `Congé de ${nameOf(leave.employeeName)} ${decision === 'validate' ? 'validé' : 'refusé'}`,
  )

export const useRequestAdvance = () =>
  useTeamMutation(createAdvance, (advance) => `Avance demandée pour ${nameOf(advance.employeeName)} · à valider`)

export const useAdvanceDecision = () =>
  useTeamMutation(
    ({ id, decision }: DecisionVariables) => decideAdvance(id, decision),
    (advance, { decision }) =>
      `Avance de ${nameOf(advance.employeeName)} ${decision === 'validate' ? 'validée' : 'refusée'}`,
  )

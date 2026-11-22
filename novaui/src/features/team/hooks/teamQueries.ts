import { getAdvanceSummary, getAdvances } from '../api/advancesApi'
import { getDepartments } from '../api/departmentsApi'
import { getEmployeeCounts, getEmployees } from '../api/employeesApi'
import { getLeavesByStatus } from '../api/leavesApi'
import { getTeamSummary } from '../api/teamSummaryApi'
import { TEAM_KEYS } from './teamQueryKeys'
import { useTeamQuery } from './useTeamQuery'

export const useTeamSummary = () => useTeamQuery(TEAM_KEYS.summary, (signal) => getTeamSummary({ signal }))

export const useEmployeeCounts = () => useTeamQuery(TEAM_KEYS.counts, (signal) => getEmployeeCounts({ signal }))

export const useEmployees = (search: string) =>
  useTeamQuery(TEAM_KEYS.employees(search), (signal) => getEmployees(search, { signal }))

export const useDepartments = (enabled: boolean) =>
  useTeamQuery(TEAM_KEYS.departments, (signal) => getDepartments({ signal }), enabled)

export const usePendingLeaves = (enabled: boolean) =>
  useTeamQuery(TEAM_KEYS.pendingLeaves, (signal) => getLeavesByStatus('DRAFT', { signal }), enabled)

export const useAdvanceSummary = (enabled: boolean) =>
  useTeamQuery(TEAM_KEYS.advanceSummary, (signal) => getAdvanceSummary({ signal }), enabled)

export const useDraftAdvances = (employeeId: number) =>
  useTeamQuery(TEAM_KEYS.draftAdvances(employeeId), (signal) =>
    getAdvances({ employeeId, status: 'DRAFT' }, { signal }),
  )

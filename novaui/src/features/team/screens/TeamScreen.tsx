import { useMemo, useState } from 'react'
import { DataTable } from '../../../components/module/DataTable'
import { ListCard } from '../../../components/module/ListCard'
import { ModulePage } from '../../../components/module/ModulePage'
import { Button } from '../../../components/ui/button'
import { useCurrentUser } from '../../../hooks/useCurrentUser'
import { teamColumns } from '../components/teamColumns'
import { teamActions } from '../components/teamActions'
import { TeamEmptyState } from '../components/TeamEmptyState'
import { TeamFilters } from '../components/TeamFilters'
import { useAdvanceSummary, useEmployeeCounts, useEmployees, useTeamSummary } from '../hooks/teamQueries'
import { useTeamAdaptation } from '../hooks/useTeamAdaptation'
import { useTeamDialogs } from '../hooks/useTeamDialogs'
import { useTeamSearch } from '../hooks/useTeamSearch'
import { visibleEmployees } from '../services/teamFilters'
import { teamKpis, teamSubtitle } from '../services/teamKpis'
import { effectiveFilter } from '../services/teamSearch'
import { TeamDialogs } from './TeamDialogs'

export const TeamScreen = () => {
  const adaptation = useTeamAdaptation()
  const isAdmin = useCurrentUser()?.role === 'ADMIN'
  const [query, setQuery] = useState('')
  const { filtre, leaveRequestsOpen, setFilter, setLeaveRequestsOpen } = useTeamSearch()
  const filter = effectiveFilter(filtre, adaptation.visible)
  const { data: employees, loading } = useEmployees(query)
  const { data: counts } = useEmployeeCounts()
  const { data: summary } = useTeamSummary()
  const { data: advances } = useAdvanceSummary(adaptation.visible)
  const dialogs = useTeamDialogs(setLeaveRequestsOpen)
  const rows = useMemo(() => visibleEmployees(employees ?? [], filter), [employees, filter])
  const columns = useMemo(() => teamColumns(adaptation), [adaptation])
  const rowActions = useMemo(
    () => teamActions({ adapted: adaptation.visible, isAdmin, onLeave: dialogs.openLeave, onAdvance: dialogs.openAdvance, onAdvanceDecisions: dialogs.openAdvanceDecisions }),
    [adaptation.visible, isAdmin, dialogs.openLeave, dialogs.openAdvance, dialogs.openAdvanceDecisions],
  )

  return (
    <ModulePage
      title="Équipe"
      subtitle={teamSubtitle(summary)}
      search={{ placeholder: 'Rechercher un employé…', defaultValue: query, onSearch: setQuery }}
      primaryAction={<Button onClick={dialogs.openEmployee}>Ajouter un employé</Button>}
      kpis={teamKpis(summary, advances, adaptation, () => setLeaveRequestsOpen(true))}
      kpiLabel="Indicateurs de l’équipe"
    >
      <ListCard
        label="Employés"
        toolbar={<TeamFilters active={filter} counts={counts} adaptation={adaptation} onChange={setFilter} />}
      >
        <DataTable
          caption="Employés triés par service"
          columns={columns}
          rows={rows}
          getRowKey={(employee) => employee.id}
          getRowLabel={(employee) => `${employee.firstName} ${employee.lastName}`}
          rowActions={rowActions}
          loading={loading}
          empty={<TeamEmptyState query={query} filtered={filter !== 'tous'} onCreate={dialogs.openEmployee} />}
        />
      </ListCard>
      <TeamDialogs
        dialog={dialogs.dialog}
        leaveRequestsOpen={leaveRequestsOpen}
        onClose={dialogs.close}
        onCloseLeaveRequests={dialogs.closeLeaveRequests}
      />
    </ModulePage>
  )
}

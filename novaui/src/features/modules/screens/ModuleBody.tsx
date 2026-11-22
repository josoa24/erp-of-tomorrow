import { Trash2 } from 'lucide-react'
import { useMemo, useState } from 'react'
import { DataTable } from '../../../components/module/DataTable'
import { ListCard } from '../../../components/module/ListCard'
import { ModulePage } from '../../../components/module/ModulePage'
import { Button } from '../../../components/ui/button'
import { useCurrentUser } from '../../../hooks/useCurrentUser'
import { ModuleEmptyState } from '../components/ModuleEmptyState'
import { ModuleFilters } from '../components/ModuleFilters'
import { moduleColumns } from '../components/moduleColumns'
import { useSalesImport } from '../hooks/useModuleActions'
import { useModuleDialogs } from '../hooks/useModuleDialogs'
import {
  hasSaleReference,
  useModuleIndicators,
  useModuleRecords,
  useModuleStatusCounts,
  useSalesImportAvailable,
} from '../hooks/useModuleScreenData'
import { moduleKpis } from '../services/moduleKpis'
import { moduleSubtitle, newRecordLabel, searchPlaceholder } from '../services/moduleLabels'
import { recordActions } from '../services/recordActions'
import type { ModuleDefinition } from '../types/moduleDefinition'
import { ModuleDialogs } from './ModuleDialogs'

export const ModuleBody = ({ definition }: { definition: ModuleDefinition }) => {
  const { slug } = definition
  const [query, setQuery] = useState('')
  const [status, setStatus] = useState<string | undefined>()
  const isAdmin = useCurrentUser()?.role === 'ADMIN'
  const records = useModuleRecords(slug, query, status)
  const indicators = useModuleIndicators(slug)
  const counts = useModuleStatusCounts(slug)
  const importable = useSalesImportAvailable(slug, hasSaleReference(definition))
  const salesImport = useSalesImport(slug)
  const dialogs = useModuleDialogs()
  const columns = useMemo(() => moduleColumns(definition), [definition])
  const rowActions = useMemo(() => recordActions(definition, dialogs.openAdvance), [definition, dialogs.openAdvance])

  return (
    <ModulePage
      title={definition.name}
      subtitle={moduleSubtitle(definition, counts.data?.all)}
      search={{ placeholder: searchPlaceholder(definition), defaultValue: query, onSearch: setQuery }}
      primaryAction={<Button onClick={dialogs.openCreate}>{newRecordLabel(definition.name)}</Button>}
      kpis={moduleKpis(definition.indicators, indicators.data)}
      kpiLabel={`Indicateurs ${definition.name}`}
    >
      <ListCard
        label={definition.name}
        toolbar={<ModuleFilters statuses={definition.statuses} counts={counts.data} active={status} onChange={setStatus} />}
      >
        <DataTable
          caption={`${definition.name}, triés par date de création`}
          columns={columns}
          rows={records.data?.items ?? []}
          getRowKey={(record) => record.id}
          getRowLabel={(record) => `${definition.name} n° ${record.id}`}
          rowActions={rowActions}
          loading={records.loading}
          empty={
            <ModuleEmptyState
              name={definition.name}
              icon={definition.icon}
              query={query}
              filtered={status !== undefined}
              importable={importable.data?.available ?? 0}
              importing={salesImport.isPending}
              onImport={() => salesImport.mutate()}
              onCreate={dialogs.openCreate}
            />
          }
        />
      </ListCard>
      {isAdmin && (
        <div className="flex justify-end">
          <Button variant="ghost" leadingIcon={<Trash2 />} onClick={dialogs.openDelete}>Supprimer le module</Button>
        </div>
      )}
      <ModuleDialogs definition={definition} dialog={dialogs.dialog} onClose={dialogs.close} />
    </ModulePage>
  )
}

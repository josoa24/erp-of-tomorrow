import { FilterBar } from '../../../components/module/FilterBar'
import { FilterChip } from '../../../components/ui/filter-chip'
import { allLabel } from '../services/moduleLabels'
import type { ModuleStatus } from '../types/moduleDefinition'
import type { StatusCounts } from '../types/moduleRecord'

interface ModuleFiltersProps {
  name: string
  statuses: ModuleStatus[]
  counts: StatusCounts | undefined
  active: string | undefined
  onChange: (status: string | undefined) => void
}

export const ModuleFilters = ({ name, statuses, counts, active, onChange }: ModuleFiltersProps) => (
  <FilterBar label="Filtrer par statut" sortHint="Triés par date de création">
    <FilterChip label={allLabel(name)} count={counts?.all} active={active === undefined} onClick={() => onChange(undefined)} />
    {statuses.map((status) => (
      <FilterChip
        key={status.key}
        label={status.label}
        count={counts?.byStatus[status.key]}
        active={active === status.key}
        onClick={() => onChange(active === status.key ? undefined : status.key)}
      />
    ))}
  </FilterBar>
)

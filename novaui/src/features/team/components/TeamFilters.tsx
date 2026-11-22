import { FilterBar } from '../../../components/module/FilterBar'
import { FilterChip } from '../../../components/ui/filter-chip'
import { TEAM_FILTER_OPTIONS } from '../constants/filterOptions'
import type { TeamFilter } from '../services/teamSearch'
import type { AdaptationView } from '../types/adaptationView'
import type { EmployeeCounts } from '../types/employee'

interface TeamFiltersProps {
  active: TeamFilter
  counts?: EmployeeCounts
  adaptation: AdaptationView
  onChange: (filter: TeamFilter) => void
}

export const TeamFilters = ({ active, counts, adaptation, onChange }: TeamFiltersProps) => (
  <FilterBar label="Filtrer l’équipe" sortHint="Triés par service">
    {TEAM_FILTER_OPTIONS.filter((option) => !option.adaptation || adaptation.visible).map((option) => (
      <FilterChip
        key={option.value}
        label={option.label}
        count={counts?.[option.countKey]}
        active={option.value === active}
        highlighted={option.adaptation && adaptation.highlighted}
        onClick={() => onChange(option.value)}
      />
    ))}
  </FilterBar>
)

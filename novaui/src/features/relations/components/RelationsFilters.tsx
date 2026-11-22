import { FilterBar } from '../../../components/module/FilterBar'
import { FilterChip } from '../../../components/ui/filter-chip'
import { FILTER_OPTIONS } from '../constants/filterOptions'
import type { RelationsFilter } from '../services/relationsSearch'
import type { AdaptationView } from '../types/adaptationView'
import type { ContactCounts } from '../types/contact'

interface RelationsFiltersProps {
  active?: RelationsFilter
  counts?: ContactCounts
  adaptation: AdaptationView
  onChange: (filter?: RelationsFilter) => void
}

export const RelationsFilters = ({ active, counts, adaptation, onChange }: RelationsFiltersProps) => (
  <FilterBar label="Filtrer les contacts" sortHint="Triés par dernier échange">
    {FILTER_OPTIONS.filter((option) => !option.adaptation || adaptation.visible).map((option) => {
      const selected = option.value === active
      return (
        <FilterChip
          key={option.label}
          label={option.label}
          count={counts?.[option.countKey]}
          active={selected}
          highlighted={option.adaptation && adaptation.highlighted}
          onClick={() => onChange(selected ? undefined : option.value)}
        />
      )
    })}
  </FilterBar>
)

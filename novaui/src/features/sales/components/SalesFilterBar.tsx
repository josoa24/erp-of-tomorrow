import { FilterBar } from '../../../components/module/FilterBar'
import { FilterChip } from '../../../components/ui/filter-chip'
import { visibleSalesFilters } from '../services/salesFilters'
import type { SaleCounts } from '../types/salesReport'
import type { SalesFilterKey } from '../types/salesQuery'

export const SALES_SORT_HINT = 'Les plus récentes d’abord'

interface SalesFilterBarProps {
  active: SalesFilterKey
  counts: SaleCounts | null
  adapted: boolean
  highlightAdapted: boolean
  onSelect: (filter: SalesFilterKey) => void
}

export const SalesFilterBar = ({ active, counts, adapted, highlightAdapted, onSelect }: SalesFilterBarProps) => (
  <FilterBar label="Filtrer les ventes" sortHint={SALES_SORT_HINT}>
    {visibleSalesFilters(adapted).map((filter) => (
      <FilterChip
        key={filter.key}
        label={filter.label}
        count={counts?.[filter.countKey]}
        active={filter.key === active}
        highlighted={highlightAdapted && filter.adaptation === true}
        onClick={() => onSelect(filter.key === active ? 'toutes' : filter.key)}
      />
    ))}
  </FilterBar>
)

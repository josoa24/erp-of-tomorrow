import { FilterBar } from '../../../components/module/FilterBar'
import { FilterChip } from '../../../components/ui/filter-chip'
import type { StockFilter, StockFilterChip } from '../services/stockFilters'

export const STOCK_SORT_HINT = 'Triés par niveau de stock'

interface StockFilterBarProps {
  chips: StockFilterChip[]
  active: StockFilter
  highlightAdapted: boolean
  onSelect: (filter: StockFilter) => void
}

export const StockFilterBar = ({ chips, active, highlightAdapted, onSelect }: StockFilterBarProps) => (
  <FilterBar label="Filtrer les produits" sortHint={STOCK_SORT_HINT}>
    {chips.map((chip) => (
      <FilterChip
        key={chip.key}
        label={chip.label}
        count={chip.count}
        active={chip.key === active}
        highlighted={highlightAdapted && chip.adapted}
        onClick={() => onSelect(chip.key === active ? 'tous' : chip.key)}
      />
    ))}
  </FilterBar>
)

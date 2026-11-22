import { ListCard } from '../../../components/module/ListCard'
import { ModulePage } from '../../../components/module/ModulePage'
import { Button } from '../../../components/ui/button'
import { Skeleton } from '../../../components/ui/skeleton'
import { StockDialogs } from '../components/StockDialogs'
import { StockEmptyState } from '../components/StockEmptyState'
import { StockFilterBar } from '../components/StockFilterBar'
import { StockTable } from '../components/StockTable'
import { useStockDialogs } from '../hooks/useStockDialogs'
import { useStockOverview } from '../hooks/useStockOverview'
import { useStockSearchParams } from '../hooks/useStockSearchParams'
import { GENERIC_STOCK_VIEW } from '../services/stockAdaptation'
import { effectiveFilter, filterStockItems, stockFilterChips } from '../services/stockFilters'
import { buildStockKpis, stockSubtitle } from '../services/stockKpis'

export const STOCK_SEARCH_PLACEHOLDER = 'Rechercher un produit…'

const KPI_PLACEHOLDER = <Skeleton className="h-8 w-24" />

export const StockScreen = () => {
  const adaptation = GENERIC_STOCK_VIEW
  const { filter, search, setFilter, setSearch } = useStockSearchParams()
  const { items, loading, counts, summary } = useStockOverview(search)
  const { dialog, openEntry, openExit, openLots, close } = useStockDialogs()
  const activeFilter = effectiveFilter(filter, adaptation.enabled)

  return (
    <ModulePage
      title="Stock"
      subtitle={summary && stockSubtitle(summary)}
      search={{ placeholder: STOCK_SEARCH_PLACEHOLDER, defaultValue: search, onSearch: setSearch }}
      primaryAction={<Button onClick={() => openEntry()}>Entrée de stock</Button>}
      kpis={buildStockKpis(summary, adaptation, KPI_PLACEHOLDER)}
      kpiLabel="Indicateurs du stock"
    >
      <ListCard
        label="Produits"
        toolbar={
          <StockFilterBar
            chips={stockFilterChips(counts, adaptation.enabled)}
            active={activeFilter}
            highlightAdapted={adaptation.highlighted}
            onSelect={setFilter}
          />
        }
      >
        <StockTable
          items={filterStockItems(items, activeFilter)}
          loading={loading}
          adaptation={adaptation}
          handlers={{ onEntry: openEntry, onExit: openExit, onLots: openLots }}
          empty={
            <StockEmptyState
              filtered={activeFilter !== 'tous'}
              search={search}
              onShowAll={() => setFilter('tous')}
              onEntry={() => openEntry()}
            />
          }
        />
      </ListCard>
      <StockDialogs dialog={dialog} adaptation={adaptation} onClose={close} />
    </ModulePage>
  )
}

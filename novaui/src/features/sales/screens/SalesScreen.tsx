import { ListCard } from '../../../components/module/ListCard'
import { ModulePage } from '../../../components/module/ModulePage'
import { Button } from '../../../components/ui/button'
import { useModuleAdaptation } from '../../eclipse/hooks/useModuleAdaptation'
import { SalesDialogs } from '../components/SalesDialogs'
import { SalesEmptyState } from '../components/SalesEmptyState'
import { SalesFilterBar } from '../components/SalesFilterBar'
import { SalesPagination } from '../components/SalesPagination'
import { SalesTable } from '../components/SalesTable'
import { useSaleRowHandlers } from '../hooks/useSaleRowHandlers'
import { useSalesCounts } from '../hooks/useSalesCounts'
import { useSalesDialogs } from '../hooks/useSalesDialogs'
import { useSalesList } from '../hooks/useSalesList'
import { useSalesOverview } from '../hooks/useSalesOverview'
import { useSalesSearch } from '../hooks/useSalesSearch'
import { currentMonthLabel } from '../services/salesDates'
import { resolveSalesFilter } from '../services/salesFilters'
import { buildSalesKpiItems } from '../services/salesKpiItems'
import { toSalesListQuery } from '../services/salesSearch'
import { pluralize } from '../../../utils/format'

export const SALES_SEARCH_PLACEHOLDER = 'Rechercher une vente, un client…'

export const SalesScreen = () => {
  const { active, preview } = useModuleAdaptation('ventes')
  const adapted = active || preview !== null
  const highlighted = preview !== null
  const { search, setFilter, setText, setPage } = useSalesSearch()
  const filter = resolveSalesFilter(search.filtre, adapted)
  const { sales, total, loading } = useSalesList(toSalesListQuery(search, adapted))
  const counts = useSalesCounts()
  const { kpis, credit, monthSalesCount } = useSalesOverview(adapted)
  const dialogs = useSalesDialogs()
  const handlers = useSaleRowHandlers(dialogs)

  return (
    <ModulePage
      title="Ventes"
      subtitle={monthSalesCount === null ? undefined : `${currentMonthLabel()} · ${pluralize(monthSalesCount, 'vente', 'ventes')}`}
      search={{ placeholder: SALES_SEARCH_PLACEHOLDER, defaultValue: search.recherche ?? '', onSearch: setText }}
      primaryAction={<Button onClick={() => dialogs.openForm()}>Nouvelle vente</Button>}
      kpis={buildSalesKpiItems({ kpis, credit, showCredit: adapted, highlightCredit: highlighted })}
      kpiLabel="Indicateurs des ventes"
    >
      <ListCard
        label="Ventes"
        toolbar={<SalesFilterBar active={filter.key} counts={counts} adapted={adapted} highlightAdapted={highlighted} onSelect={setFilter} />}
      >
        <SalesTable
          sales={sales}
          loading={loading}
          adapted={adapted}
          highlighted={highlighted}
          handlers={handlers}
          empty={<SalesEmptyState filtered={filter.key !== 'toutes'} search={search.recherche} onShowAll={() => setFilter('toutes')} onCreate={() => dialogs.openForm()} />}
        />
        <SalesPagination page={search.page ?? 1} total={total} onPage={setPage} />
      </ListCard>
      <SalesDialogs dialog={dialogs.dialog} adapted={adapted} highlighted={highlighted} onClose={dialogs.close} />
    </ModulePage>
  )
}

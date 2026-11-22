import { ChartColumn, Percent, Receipt, TrendingUp } from 'lucide-react'
import type { KpiItem } from '../../../components/module/types'
import { formatCompactAmount, pluralize } from '../../../utils/format'
import type { CreditSummary, SalesKpis } from '../types/salesReport'
import { previousMonthLabel } from './salesDates'
import { formatAmount, formatChange } from './salesFormat'

export const KPI_PLACEHOLDER = '—'

export interface SalesKpiSource {
  kpis: SalesKpis | null
  credit: CreditSummary | null
  showCredit: boolean
  highlightCredit: boolean
  now?: Date
}

const creditItem = ({ credit, highlightCredit }: SalesKpiSource): KpiItem => ({
  key: 'credit',
  icon: Percent,
  tone: 'mint',
  label: 'Crédit en cours',
  value: credit ? formatCompactAmount(credit.outstanding) : KPI_PLACEHOLDER,
  hint: credit
    ? `${pluralize(credit.customers, 'client', 'clients')} · ${credit.overdueCustomers} en retard`
    : undefined,
  highlighted: highlightCredit,
})

export const buildSalesKpiItems = (source: SalesKpiSource): KpiItem[] => {
  const { kpis, now = new Date() } = source
  const items: KpiItem[] = [
    {
      key: 'revenue',
      icon: ChartColumn,
      label: 'Chiffre d’affaires',
      value: kpis ? formatCompactAmount(kpis.monthRevenue) : KPI_PLACEHOLDER,
      hint: kpis ? formatChange(kpis.monthRevenue, kpis.previousMonthRevenue, `vs ${previousMonthLabel(now)}`) : undefined,
    },
    {
      key: 'today',
      icon: Receipt,
      tone: 'mint',
      label: 'Ventes du jour',
      value: kpis ? kpis.todaySalesCount : KPI_PLACEHOLDER,
      hint: kpis ? `${formatCompactAmount(kpis.todayCollected)} encaissés` : undefined,
    },
    {
      key: 'basket',
      icon: TrendingUp,
      tone: 'blush',
      label: 'Panier moyen',
      value: kpis ? formatAmount(kpis.averageBasket) : KPI_PLACEHOLDER,
      hint: kpis ? formatChange(kpis.averageBasket, kpis.previousMonthAverageBasket, 'ce mois') : undefined,
    },
  ]
  return source.showCredit ? [...items, creditItem(source)] : items
}

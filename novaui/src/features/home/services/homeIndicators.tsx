import { Package, ShoppingCart, Users } from 'lucide-react'
import type { KpiItem } from '../../../components/module/types'
import { formatCompactAmount, formatDecimal, formatSignedPercent, pluralize } from '../../../utils/format'
import type { StockSummary } from '../../stock/types/stock'
import type { TeamSummary } from '../../team/types/teamSummary'
import type { SalesKpis } from '../types/home'

export const UNAVAILABLE = 'indisponible'
export const PENDING = '—'

export interface Block<T> {
  data: T | undefined
  loading: boolean
  failed: boolean
}

const hintOrState = (block: Block<unknown>, hint: string | undefined): string | undefined =>
  block.failed ? UNAVAILABLE : hint

export const salesIndicator = (block: Block<SalesKpis>): KpiItem => {
  const { data } = block
  const previous = data?.sameWeekdayLastWeekRevenue ?? 0
  const trend = data && previous > 0 ? ((data.todayRevenue - previous) / previous) * 100 : null
  return {
    key: 'sales',
    icon: ShoppingCart,
    tone: 'lilac',
    label: 'Ventes du jour',
    value: data ? formatCompactAmount(data.todayRevenue) : PENDING,
    hint: hintOrState(block, trend === null ? data && pluralize(data.todaySalesCount, 'vente', 'ventes') : `${formatSignedPercent(trend)} vs semaine dernière`),
  }
}

export const stockIndicator = (block: Block<StockSummary>): KpiItem => {
  const { data } = block
  return {
    key: 'stock',
    icon: Package,
    tone: 'mint',
    label: 'Stock disponible',
    value: data ? `${formatDecimal(data.availabilityRate * 100)} %` : PENDING,
    hint: hintOrState(block, data && `${pluralize(data.lowStockProducts, 'produit', 'produits')} en alerte`),
  }
}

export const teamIndicator = (block: Block<TeamSummary>): KpiItem => {
  const { data } = block
  return {
    key: 'team',
    icon: Users,
    tone: 'blush',
    label: 'Équipe',
    value: data?.totalEmployees ?? PENDING,
    hint: hintOrState(block, data && `${data.onLeaveToday} en congé aujourd’hui`),
  }
}

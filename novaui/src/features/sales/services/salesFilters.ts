import type { SaleCounts } from '../types/salesReport'
import type { SalesFilterKey, SalesListQuery } from '../types/salesQuery'

export interface SalesFilter {
  key: SalesFilterKey
  label: string
  countKey: keyof SaleCounts
  criteria: Pick<SalesListQuery, 'paymentStatus' | 'paymentMethod'>
  adaptation?: boolean
}

export const DEFAULT_SALES_FILTER: SalesFilterKey = 'toutes'

export const SALES_FILTERS: readonly SalesFilter[] = [
  { key: 'toutes', label: 'Toutes', countKey: 'all', criteria: {} },
  {
    key: 'a-encaisser',
    label: 'À encaisser',
    countKey: 'toCollect',
    criteria: { paymentStatus: ['TO_COLLECT', 'OVERDUE'] },
  },
  { key: 'payees', label: 'Payées', countKey: 'paid', criteria: { paymentStatus: ['PAID'] } },
  { key: 'annulees', label: 'Annulées', countKey: 'cancelled', criteria: { paymentStatus: ['CANCELLED'] } },
  {
    key: 'a-credit',
    label: 'À crédit',
    countKey: 'credit',
    criteria: { paymentMethod: 'CREDIT' },
    adaptation: true,
  },
]

export const isSalesFilterKey = (value: unknown): value is SalesFilterKey =>
  SALES_FILTERS.some((filter) => filter.key === value)

export const visibleSalesFilters = (adapted: boolean): SalesFilter[] =>
  SALES_FILTERS.filter((filter) => adapted || !filter.adaptation)

export const resolveSalesFilter = (key: SalesFilterKey | undefined, adapted: boolean): SalesFilter =>
  visibleSalesFilters(adapted).find((filter) => filter.key === key) ?? SALES_FILTERS[0]

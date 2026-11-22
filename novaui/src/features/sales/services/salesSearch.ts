import type { SalesListQuery, SalesSearch } from '../types/salesQuery'
import { DEFAULT_SALES_FILTER, isSalesFilterKey, resolveSalesFilter } from './salesFilters'

export const SALES_PAGE_SIZE = 20

const toText = (value: unknown): string | undefined => {
  if (typeof value === 'number') return String(value)
  if (typeof value !== 'string') return undefined
  return value.trim() || undefined
}

const toPage = (value: unknown): number | undefined => {
  const page = typeof value === 'string' ? Number(value) : value
  return typeof page === 'number' && Number.isInteger(page) && page > 1 ? page : undefined
}

export const validateSalesSearch = (search: Record<string, unknown>): SalesSearch => {
  const filtre = isSalesFilterKey(search.filtre) && search.filtre !== DEFAULT_SALES_FILTER ? search.filtre : undefined
  return { filtre, recherche: toText(search.recherche), page: toPage(search.page) }
}

export const toSalesListQuery = (search: SalesSearch, adapted: boolean): SalesListQuery => ({
  ...resolveSalesFilter(search.filtre, adapted).criteria,
  search: search.recherche,
  page: (search.page ?? 1) - 1,
  size: SALES_PAGE_SIZE,
})

export const pageCount = (total: number): number => Math.max(1, Math.ceil(total / SALES_PAGE_SIZE))

export const pageBounds = (page: number, total: number): { first: number; last: number } => ({
  first: total === 0 ? 0 : (page - 1) * SALES_PAGE_SIZE + 1,
  last: Math.min(page * SALES_PAGE_SIZE, total),
})

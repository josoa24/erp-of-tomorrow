import type { SalesListQuery } from '../types/salesQuery'

export const SALES_QUERY_KEY = ['sales'] as const

export const salesQueryKeys = {
  list: (query: SalesListQuery) => [...SALES_QUERY_KEY, 'list', query] as const,
  counts: [...SALES_QUERY_KEY, 'counts'] as const,
  kpis: [...SALES_QUERY_KEY, 'kpis'] as const,
  monthSummary: (from: string, to: string) => [...SALES_QUERY_KEY, 'month-summary', from, to] as const,
  credit: [...SALES_QUERY_KEY, 'credit-summary'] as const,
  payments: (saleId: number) => [...SALES_QUERY_KEY, 'payments', saleId] as const,
  catalog: (search: string) => [...SALES_QUERY_KEY, 'catalog', search] as const,
  customers: (search: string) => [...SALES_QUERY_KEY, 'customers', search] as const,
}

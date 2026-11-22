import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { getStockCounts, getStockStatus } from '../api/stockStatusApi'
import { getStockSummary } from '../api/stockSummaryApi'
import type { StockStatusItem } from '../types/stock'
import { stockQueryKeys } from './stockQueryKeys'
import { useErrorRedirect } from './useErrorRedirect'

const NO_ITEMS: StockStatusItem[] = []

export const useStockOverview = (search: string) => {
  const status = useQuery({
    queryKey: stockQueryKeys.status(search),
    queryFn: ({ signal }) => getStockStatus(search, { signal }),
    placeholderData: keepPreviousData,
  })
  const counts = useQuery({
    queryKey: stockQueryKeys.counts(),
    queryFn: ({ signal }) => getStockCounts({ signal }),
  })
  const summary = useQuery({
    queryKey: stockQueryKeys.summary(),
    queryFn: ({ signal }) => getStockSummary({ signal }),
  })
  useErrorRedirect(status.error ?? counts.error ?? summary.error)

  return {
    items: status.data ?? NO_ITEMS,
    loading: status.isPending,
    counts: counts.data,
    summary: summary.data,
  }
}

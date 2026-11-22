import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { listSales } from '../api/salesApi'
import type { Sale } from '../types/sale'
import type { SalesListQuery } from '../types/salesQuery'
import { salesQueryKeys } from './salesQueryKeys'
import { useRedirectOnError } from './useRedirectOnError'

const NO_SALES: Sale[] = []

export const useSalesList = (query: SalesListQuery) => {
  const result = useQuery({
    queryKey: salesQueryKeys.list(query),
    queryFn: ({ signal }) => listSales(query, { signal }),
    placeholderData: keepPreviousData,
  })
  useRedirectOnError(result.error)

  return {
    sales: result.data?.items ?? NO_SALES,
    total: result.data?.total ?? 0,
    loading: result.isPending,
  }
}

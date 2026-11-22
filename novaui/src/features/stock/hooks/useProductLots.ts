import { useQuery } from '@tanstack/react-query'
import { getProductLots } from '../api/stockLotsApi'
import type { StockLot } from '../types/stock'
import { stockQueryKeys } from './stockQueryKeys'
import { useErrorRedirect } from './useErrorRedirect'

const NO_LOTS: StockLot[] = []

export const useProductLots = (productId: number) => {
  const query = useQuery({
    queryKey: stockQueryKeys.lots(productId),
    queryFn: ({ signal }) => getProductLots(productId, { signal }),
  })
  useErrorRedirect(query.error)

  return { lots: query.data ?? NO_LOTS, loading: query.isPending }
}

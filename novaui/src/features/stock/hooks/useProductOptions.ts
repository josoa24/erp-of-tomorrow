import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { searchStockProducts } from '../api/stockProductsApi'
import type { StockProduct } from '../types/stock'
import { stockQueryKeys } from './stockQueryKeys'
import { useErrorRedirect } from './useErrorRedirect'

const NO_PRODUCTS: StockProduct[] = []

export const useProductOptions = (search: string, enabled: boolean) => {
  const query = useQuery({
    queryKey: stockQueryKeys.products(search),
    queryFn: ({ signal }) => searchStockProducts(search, { signal }),
    placeholderData: keepPreviousData,
    enabled,
  })
  useErrorRedirect(query.error)

  return { products: query.data ?? NO_PRODUCTS, loading: enabled && query.isPending }
}

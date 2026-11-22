import { useQuery } from '@tanstack/react-query'
import { searchCatalogProducts } from '../api/catalogApi'
import type { CatalogProduct } from '../types/catalog'
import { salesQueryKeys } from './salesQueryKeys'
import { useRedirectOnError } from './useRedirectOnError'

export const PICKER_RESULTS_LIMIT = 6
const NONE: CatalogProduct[] = []

export const useCatalogProducts = (search: string) => {
  const result = useQuery({
    queryKey: salesQueryKeys.catalog(search),
    queryFn: ({ signal }) => searchCatalogProducts(search, { signal }),
  })
  useRedirectOnError(result.error)

  return { products: (result.data ?? NONE).slice(0, PICKER_RESULTS_LIMIT), loading: result.isFetching }
}

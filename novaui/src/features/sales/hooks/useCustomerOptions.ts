import { useQuery } from '@tanstack/react-query'
import { searchCustomerOptions } from '../api/customerOptionsApi'
import type { CustomerOption } from '../types/catalog'
import { salesQueryKeys } from './salesQueryKeys'
import { useRedirectOnError } from './useRedirectOnError'

const NONE: CustomerOption[] = []

export const useCustomerOptions = (search: string) => {
  const result = useQuery({
    queryKey: salesQueryKeys.customers(search),
    queryFn: ({ signal }) => searchCustomerOptions(search, { signal }),
  })
  useRedirectOnError(result.error)

  return { customers: result.data ?? NONE, loading: result.isFetching }
}

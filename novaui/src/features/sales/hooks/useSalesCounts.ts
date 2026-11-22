import { useQuery } from '@tanstack/react-query'
import { getSaleCounts } from '../api/salesReportsApi'
import { salesQueryKeys } from './salesQueryKeys'
import { useRedirectOnError } from './useRedirectOnError'

export const useSalesCounts = () => {
  const result = useQuery({
    queryKey: salesQueryKeys.counts,
    queryFn: ({ signal }) => getSaleCounts({ signal }),
  })
  useRedirectOnError(result.error)

  return result.data ?? null
}

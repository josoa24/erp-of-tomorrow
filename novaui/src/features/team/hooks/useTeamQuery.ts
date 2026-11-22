import { useQuery, type QueryKey } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useApiError } from '../../../hooks/useApiError'

export interface TeamQueryResult<T> {
  data: T | undefined
  loading: boolean
}

export const useTeamQuery = <T>(
  queryKey: QueryKey,
  load: (signal: AbortSignal) => Promise<T>,
  enabled = true,
): TeamQueryResult<T> => {
  const handleError = useApiError()
  const query = useQuery({ queryKey, queryFn: ({ signal }) => load(signal), enabled })

  useEffect(() => {
    if (query.error) handleError(query.error)
  }, [query.error, handleError])

  return { data: query.data, loading: enabled && query.isPending }
}

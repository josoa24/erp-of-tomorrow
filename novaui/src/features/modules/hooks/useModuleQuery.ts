import { useQuery, type QueryKey } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useApiError } from '../../../hooks/useApiError'

export const MODULES_QUERY_KEY = ['modules'] as const

export interface ModuleQueryResult<T> {
  data: T | undefined
  loading: boolean
  fetching: boolean
}

export const useModuleQuery = <T>(
  queryKey: QueryKey,
  load: (signal: AbortSignal) => Promise<T>,
  enabled = true,
): ModuleQueryResult<T> => {
  const handleError = useApiError()
  const query = useQuery({
    queryKey: [...MODULES_QUERY_KEY, ...queryKey],
    queryFn: ({ signal }) => load(signal),
    enabled,
    retry: false,
    placeholderData: (previous) => previous,
  })

  useEffect(() => {
    if (query.error) handleError(query.error)
  }, [query.error, handleError])

  return { data: query.data, loading: enabled && query.isPending, fetching: query.isFetching }
}

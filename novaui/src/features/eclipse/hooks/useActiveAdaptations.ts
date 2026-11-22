import { useQuery } from '@tanstack/react-query'
import { useEffect, useRef } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { NO_ACTIVE_ADAPTATIONS, loadActiveAdaptations } from '../services/activeAdaptations'

export const ADAPTATIONS_QUERY_KEY = ['adaptations'] as const

export const useActiveAdaptations = () => {
  const handleError = useApiError()
  const reportedError = useRef<unknown>(null)
  const { data, error, isFetching } = useQuery({
    queryKey: ADAPTATIONS_QUERY_KEY,
    queryFn: ({ signal }) => loadActiveAdaptations({ signal }),
    retry: false,
    staleTime: 60_000,
  })

  useEffect(() => {
    if (!error || isFetching || reportedError.current === error) return
    reportedError.current = error
    handleError(error)
  }, [error, isFetching, handleError])

  return data ?? NO_ACTIVE_ADAPTATIONS
}

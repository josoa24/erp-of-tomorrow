import { useQuery } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { getUserCounts } from '../api/usersApi'
import { usersQueryKeys } from './usersQueryKeys'

export const useUsersCounts = () => {
  const handleError = useApiError()
  const result = useQuery({ queryKey: usersQueryKeys.counts, queryFn: ({ signal }) => getUserCounts({ signal }) })
  useEffect(() => {
    if (result.error) handleError(result.error)
  }, [result.error, handleError])

  return result.data ?? null
}

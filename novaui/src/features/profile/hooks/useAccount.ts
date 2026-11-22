import { useQuery } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { getCurrentAccount } from '../../../services/api/authApi'

export const useAccount = () => {
  const handleError = useApiError()
  const query = useQuery({ queryKey: ['profile', 'account'], queryFn: ({ signal }) => getCurrentAccount({ signal }) })

  useEffect(() => {
    if (query.error) handleError(query.error)
  }, [query.error, handleError])

  return query.data
}

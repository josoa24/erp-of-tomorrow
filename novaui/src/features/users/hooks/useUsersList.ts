import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { useApiError } from '../../../hooks/useApiError'
import { useEffect } from 'react'
import { listUsers } from '../api/usersApi'
import type { ManagedUser, UsersListQuery } from '../types/user'
import { usersQueryKeys } from './usersQueryKeys'

const NO_USERS: ManagedUser[] = []

export const useUsersList = (query: UsersListQuery) => {
  const handleError = useApiError()
  const result = useQuery({
    queryKey: usersQueryKeys.list(query),
    queryFn: ({ signal }) => listUsers(query, { signal }),
    placeholderData: keepPreviousData,
  })
  useEffect(() => {
    if (result.error) handleError(result.error)
  }, [result.error, handleError])

  return { users: result.data?.items ?? NO_USERS, total: result.data?.total ?? 0, loading: result.isPending, settled: !result.isPending && !result.isPlaceholderData }
}

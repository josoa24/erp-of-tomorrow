import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback, useState } from 'react'
import { ROUTES } from '../../../navigation/routes'
import type { Role } from '../../../types/auth'
import { DEFAULT_USERS_FILTER } from '../services/usersFilters'
import { validateUsersSearch } from '../services/usersSearch'
import type { UsersFilterKey, UsersSearch } from '../types/user'

export const useUsersSearch = () => {
  const navigate = useNavigate({ from: ROUTES.users })
  const [resetKey, setResetKey] = useState(0)
  const search = validateUsersSearch(useSearch({ strict: false }))

  const update = useCallback(
    (patch: UsersSearch) => {
      return navigate({ to: '.', replace: true, search: (previous) => ({ ...previous, ...patch }) })
    },
    [navigate],
  )

  return {
    search,
    resetKey,
    reset: useCallback(() => {
      void update({ filtre: undefined, role: undefined, recherche: undefined, page: undefined }).then(() => setResetKey((key) => key + 1))
    }, [update]),
    setFilter: useCallback(
      (filter: UsersFilterKey) => update({ filtre: filter === DEFAULT_USERS_FILTER ? undefined : filter, page: undefined }),
      [update],
    ),
    setRoles: useCallback((roles: Role[]) => update({ role: roles.length > 0 ? roles : undefined, page: undefined }), [update]),
    setText: useCallback((text: string) => update({ recherche: text || undefined, page: undefined }), [update]),
    setPage: useCallback((page: number) => update({ page: page > 1 ? page : undefined }), [update]),
  }
}

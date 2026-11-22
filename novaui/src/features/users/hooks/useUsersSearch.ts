import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback } from 'react'
import { ROUTES } from '../../../navigation/routes'
import type { Role } from '../../../types/auth'
import { DEFAULT_USERS_FILTER } from '../services/usersFilters'
import { validateUsersSearch } from '../services/usersSearch'
import type { UsersFilterKey, UsersSearch } from '../types/user'

export const useUsersSearch = () => {
  const navigate = useNavigate({ from: ROUTES.users })
  const search = validateUsersSearch(useSearch({ strict: false }))

  const update = useCallback(
    (patch: UsersSearch) => {
      void navigate({ to: '.', replace: true, search: (previous) => ({ ...previous, ...patch }) })
    },
    [navigate],
  )

  return {
    search,
    reset: useCallback(() => update({ filtre: undefined, role: undefined, recherche: undefined, page: undefined }), [update]),
    setFilter: useCallback(
      (filter: UsersFilterKey) => update({ filtre: filter === DEFAULT_USERS_FILTER ? undefined : filter, page: undefined }),
      [update],
    ),
    setRole: useCallback((role: Role | undefined) => update({ role, page: undefined }), [update]),
    setText: useCallback((text: string) => update({ recherche: text || undefined, page: undefined }), [update]),
    setPage: useCallback((page: number) => update({ page: page > 1 ? page : undefined }), [update]),
  }
}

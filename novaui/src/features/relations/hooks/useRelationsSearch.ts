import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback } from 'react'
import { ROUTES } from '../../../navigation/routes'
import type { RelationsFilter } from '../services/relationsSearch'

export const useRelationsSearch = () => {
  const navigate = useNavigate({ from: ROUTES.relations })
  const filter = useSearch({ from: '/protected/relations', select: (search) => search.filtre })
  const query = useSearch({ from: '/protected/relations', select: (search) => search.q ?? '' })

  const setFilter = useCallback(
    (filtre?: RelationsFilter) => {
      void navigate({ to: '.', search: (previous) => ({ ...previous, filtre }) })
    },
    [navigate],
  )

  const setQuery = useCallback(
    (q: string) => {
      void navigate({ to: '.', search: (previous) => ({ ...previous, q: q === '' ? undefined : q }), replace: true })
    },
    [navigate],
  )

  return { filter, query, setFilter, setQuery }
}

import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback } from 'react'
import type { RelationsFilter } from '../services/relationsSearch'

export const useRelationsSearch = () => {
  const navigate = useNavigate()
  const filter = useSearch({ strict: false, select: (search) => search.filtre })
  const query = useSearch({ strict: false, select: (search) => search.q ?? '' })

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

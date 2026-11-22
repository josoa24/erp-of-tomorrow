import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback } from 'react'
import { ROUTES } from '../../../navigation/routes'
import { DEFAULT_SALES_FILTER } from '../services/salesFilters'
import { validateSalesSearch } from '../services/salesSearch'
import type { SalesFilterKey, SalesSearch } from '../types/salesQuery'

export const useSalesSearch = () => {
  const navigate = useNavigate({ from: ROUTES.sales })
  const search = validateSalesSearch(useSearch({ strict: false }))

  const update = useCallback(
    (patch: SalesSearch) => {
      void navigate({ to: '.', replace: true, search: (previous) => ({ ...previous, ...patch }) })
    },
    [navigate],
  )

  const setFilter = useCallback(
    (filter: SalesFilterKey) =>
      update({ filtre: filter === DEFAULT_SALES_FILTER ? undefined : filter, page: undefined }),
    [update],
  )
  const setText = useCallback((text: string) => update({ recherche: text || undefined, page: undefined }), [update])
  const setPage = useCallback((page: number) => update({ page: page > 1 ? page : undefined }), [update])

  return { search, setFilter, setText, setPage }
}

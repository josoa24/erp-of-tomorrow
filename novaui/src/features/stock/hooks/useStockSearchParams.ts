import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback } from 'react'
import { ROUTES } from '../../../navigation/routes'
import { isStockFilter, type StockFilter } from '../services/stockFilters'

export const useStockSearchParams = () => {
  const navigate = useNavigate({ from: ROUTES.stock })
  const rawFilter = useSearch({ strict: false, select: (search) => search.filtre })
  const search = useSearch({ strict: false, select: (search) => search.recherche ?? '' })
  const filter: StockFilter = isStockFilter(rawFilter) ? rawFilter : 'tous'

  const setFilter = useCallback(
    (next: StockFilter) => {
      void navigate({
        to: ROUTES.stock,
        search: (previous) => ({ ...previous, filtre: next === 'tous' ? undefined : next }),
      })
    },
    [navigate],
  )

  const setSearch = useCallback(
    (next: string) => {
      void navigate({
        to: ROUTES.stock,
        replace: true,
        search: (previous) => ({ ...previous, recherche: next === '' ? undefined : next }),
      })
    },
    [navigate],
  )

  return { filter, search, setFilter, setSearch }
}

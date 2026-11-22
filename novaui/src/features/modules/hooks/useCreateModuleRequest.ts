import { useNavigate, useSearch } from '@tanstack/react-router'
import { useCallback } from 'react'
import { CREATE_MODULE_SEARCH_VALUE } from '../../../navigation/shellSearch'

export const useCreateModuleRequest = () => {
  const navigate = useNavigate()
  const requested = useSearch({ strict: false, select: (search) => search.creer === CREATE_MODULE_SEARCH_VALUE })

  const open = useCallback(() => {
    void navigate({ to: '.', search: (previous) => ({ ...previous, creer: CREATE_MODULE_SEARCH_VALUE }) })
  }, [navigate])

  const close = useCallback(() => {
    void navigate({ to: '.', search: (previous) => ({ ...previous, creer: undefined }) })
  }, [navigate])

  return { requested, open, close }
}

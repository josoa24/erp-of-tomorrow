import { useQuery } from '@tanstack/react-query'
import { useEffect } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { ApiError } from '../../../types/apiError'
import { searchReference } from '../api/moduleReferencesApi'
import { toReferenceOptions } from '../services/referenceOptions'
import type { ReferenceTarget } from '../types/moduleDefinition'
import { MODULES_QUERY_KEY } from './useModuleQuery'

const isForbidden = (error: unknown): boolean => error instanceof ApiError && error.status === 403

export const useReferenceOptions = (target: ReferenceTarget, search: string) => {
  const handleError = useApiError()
  const query = useQuery({
    queryKey: [...MODULES_QUERY_KEY, 'references', target, search],
    queryFn: async ({ signal }) => toReferenceOptions(target, await searchReference(target, search, { signal })),
    retry: false,
    placeholderData: (previous) => previous,
  })
  const forbidden = isForbidden(query.error)

  useEffect(() => {
    if (query.error && !forbidden) handleError(query.error)
  }, [query.error, forbidden, handleError])

  return { options: query.data ?? [], loading: query.isPending, forbidden }
}

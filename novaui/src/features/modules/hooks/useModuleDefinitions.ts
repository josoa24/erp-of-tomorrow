import { useQuery } from '@tanstack/react-query'
import { getModuleDefinitions } from '../api/moduleDefinitionsApi'
import type { ModuleDefinitionSummary } from '../types/moduleDefinition'

export const MODULE_DEFINITIONS_QUERY_KEY = ['modules', 'definitions'] as const

const EMPTY: ModuleDefinitionSummary[] = []

export const useModuleDefinitions = () => {
  const query = useQuery({
    queryKey: MODULE_DEFINITIONS_QUERY_KEY,
    queryFn: ({ signal }) => getModuleDefinitions({ signal }),
    retry: false,
    staleTime: 60_000,
  })

  return {
    modules: query.data ?? EMPTY,
    loading: query.isPending,
    unavailable: query.isError,
  }
}

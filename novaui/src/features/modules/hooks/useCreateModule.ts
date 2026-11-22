import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useLocation, useNavigate } from '@tanstack/react-router'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { ROUTES } from '../../../navigation/routes'
import { createModuleDefinition, deleteModuleDefinition } from '../api/moduleDefinitionsApi'
import type { ModuleDefinitionRequest } from '../types/moduleDefinition'
import { MODULES_QUERY_KEY } from './useModuleQuery'

export const useCreateModule = (onCreated: () => void) => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()
  const previous = useLocation({ select: (location) => location.pathname })

  const undo = async (slug: string) => {
    try {
      await deleteModuleDefinition(slug)
      await queryClient.invalidateQueries({ queryKey: MODULES_QUERY_KEY })
      await navigate({ to: previous === ROUTES.customModule ? ROUTES.home : previous })
    } catch (error) {
      handleError(error)
    }
  }

  const mutation = useMutation({
    mutationFn: (request: ModuleDefinitionRequest) => createModuleDefinition(request),
    onSuccess: async (definition) => {
      onCreated()
      await queryClient.invalidateQueries({ queryKey: MODULES_QUERY_KEY })
      await navigate({ to: ROUTES.customModule, params: { slug: definition.slug } })
      showToast({
        message: `Module « ${definition.name} » créé. Il est dans Mes modules.`,
        actionLabel: 'Annuler',
        onAction: () => void undo(definition.slug),
      })
    },
    onError: handleError,
  })

  return { create: mutation.mutate, creating: mutation.isPending }
}

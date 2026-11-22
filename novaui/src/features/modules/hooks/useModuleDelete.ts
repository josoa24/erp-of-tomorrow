import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { ROUTES } from '../../../navigation/routes'
import { deleteModuleDefinition } from '../api/moduleDefinitionsApi'
import { MODULES_QUERY_KEY } from './useModuleQuery'

export const useModuleDelete = (slug: string, name: string, onDone: () => void) => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()

  return useMutation({
    mutationFn: () => deleteModuleDefinition(slug),
    onSuccess: async () => {
      onDone()
      await navigate({ to: ROUTES.home, replace: true })
      queryClient.removeQueries({ queryKey: [...MODULES_QUERY_KEY, 'definition', slug] })
      await queryClient.invalidateQueries({ queryKey: MODULES_QUERY_KEY })
      showToast({ message: `Module « ${name} » supprimé` })
    },
    onError: handleError,
  })
}

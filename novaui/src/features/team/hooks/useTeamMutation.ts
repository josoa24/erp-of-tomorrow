import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { TEAM_QUERY_KEY } from './teamQueryKeys'

export const useTeamMutation = <Variables, Result>(
  mutationFn: (variables: Variables) => Promise<Result>,
  successMessage: (result: Result, variables: Variables) => string,
) => {
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()

  return useMutation({
    mutationFn,
    onSuccess: async (result, variables) => {
      await queryClient.invalidateQueries({ queryKey: TEAM_QUERY_KEY })
      showToast({ message: successMessage(result, variables) })
    },
    onError: handleError,
  })
}

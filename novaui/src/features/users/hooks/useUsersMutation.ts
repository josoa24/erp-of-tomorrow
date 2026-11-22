import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { USERS_QUERY_KEY } from './usersQueryKeys'

export interface UsersMutationOptions<Args, Result> {
  run: (args: Args) => Promise<Result>
  message?: (result: Result, args: Args) => string
}

export const useUsersMutation = <Args, Result>({ run, message }: UsersMutationOptions<Args, Result>) => {
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()

  return useMutation({
    mutationFn: run,
    onSuccess: (result, args) => {
      void queryClient.invalidateQueries({ queryKey: USERS_QUERY_KEY })
      if (message) showToast({ message: message(result, args) })
    },
    onError: handleError,
  })
}

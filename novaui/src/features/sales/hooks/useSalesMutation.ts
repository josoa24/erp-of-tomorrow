import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { SALES_QUERY_KEY } from './salesQueryKeys'

export interface SalesMutationOptions<Args, Result> {
  run: (args: Args) => Promise<Result>
  message: (result: Result, args: Args) => string
}

export const useSalesMutation = <Args, Result>({ run, message }: SalesMutationOptions<Args, Result>) => {
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()

  return useMutation({
    mutationFn: run,
    onSuccess: (result, args) => {
      void queryClient.invalidateQueries({ queryKey: SALES_QUERY_KEY })
      showToast({ message: message(result, args) })
    },
    onError: handleError,
  })
}

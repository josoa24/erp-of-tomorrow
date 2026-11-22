import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { recordMovement } from '../api/stockMovementsApi'
import type { MovementRequest } from '../types/stock'
import { STOCK_QUERY_KEY } from './stockQueryKeys'

interface MovementInput {
  productId: number
  movement: MovementRequest
}

export const MOVEMENT_SUCCESS: Record<MovementRequest['type'], string> = {
  IN: 'Entrée de stock enregistrée',
  OUT: 'Sortie de stock enregistrée',
}

export const useRecordMovement = (onRecorded: () => void) => {
  const queryClient = useQueryClient()
  const { showToast } = useToast()
  const handleError = useApiError()

  const mutation = useMutation({
    mutationFn: ({ productId, movement }: MovementInput) => recordMovement(productId, movement),
    onSuccess: (_response, { movement }) => {
      showToast({ message: MOVEMENT_SUCCESS[movement.type] })
      onRecorded()
      void queryClient.invalidateQueries({ queryKey: STOCK_QUERY_KEY })
    },
    onError: handleError,
  })

  return { record: mutation.mutate, recording: mutation.isPending }
}

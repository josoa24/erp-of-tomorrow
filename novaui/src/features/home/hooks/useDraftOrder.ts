import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { createDraftOrder, getProductPrices } from '../api/homeApi'
import type { Replenishment } from '../types/home'

export const useDraftOrder = (item: Replenishment, onDone: () => void) => {
  const queryClient = useQueryClient()
  const handleError = useApiError()
  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: async () => {
      const prices = await getProductPrices(item.productId)
      return createDraftOrder({
        supplierId: item.suggestedSupplierId ?? 0,
        lines: [{ productId: item.productId, quantity: item.recommendedQuantity ?? 0, unitPrice: prices.purchasePrice ?? prices.unitPrice }],
      })
    },
    onSuccess: async () => {
      showToast({ message: `Commande brouillon créée pour ${item.productName}` })
      onDone()
      await queryClient.invalidateQueries({ queryKey: ['home'] })
    },
    onError: handleError,
  })

  return { order: () => mutation.mutate(), ordering: mutation.isPending }
}

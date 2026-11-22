import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useToast } from '../../../components/ui/toast-context'
import { useApiError } from '../../../hooks/useApiError'
import { createPurchaseOrder, receivePurchaseOrder, validatePurchaseOrder } from '../api/purchaseOrdersApi'
import { orderNumber } from '../services/orderFormat'
import type { PurchaseOrder } from '../types/purchaseOrder'
import { STOCK_QUERY_KEY } from './stockQueryKeys'

const useOrderMutation = <Input,>(run: (input: Input) => Promise<PurchaseOrder>, message: (order: PurchaseOrder) => string, onDone?: () => void) => {
  const queryClient = useQueryClient()
  const { showToast } = useToast()
  const handleError = useApiError()

  return useMutation({
    mutationFn: run,
    onSuccess: (order) => {
      showToast({ message: message(order) })
      onDone?.()
      void queryClient.invalidateQueries({ queryKey: STOCK_QUERY_KEY })
    },
    onError: handleError,
  })
}

export const useCreateOrder = (onDone: () => void) =>
  useOrderMutation(createPurchaseOrder, (order) => `Commande ${orderNumber(order)} enregistrée en brouillon`, onDone)

export const useValidateOrder = (onDone: () => void) =>
  useOrderMutation(validatePurchaseOrder, (order) => `Commande ${orderNumber(order)} validée : elle ne peut plus changer`, onDone)

export const useReceiveOrder = (onDone: () => void) =>
  useOrderMutation(receivePurchaseOrder, (order) => `Commande ${orderNumber(order)} réceptionnée : le stock est mis à jour`, onDone)

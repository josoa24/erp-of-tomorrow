import { useCallback, useState } from 'react'
import type { PurchaseOrder } from '../types/purchaseOrder'

export type OrderDialog =
  | { type: 'create' }
  | { type: 'view'; order: PurchaseOrder }
  | { type: 'validate'; order: PurchaseOrder }
  | { type: 'receive'; order: PurchaseOrder }

export const useOrderDialogs = () => {
  const [dialog, setDialog] = useState<OrderDialog | null>(null)
  const open = useCallback((next: OrderDialog) => setDialog(next), [])
  return { dialog, open, close: useCallback(() => setDialog(null), []) }
}

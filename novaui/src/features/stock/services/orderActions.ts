import { CheckCheck, Eye, PackageCheck } from 'lucide-react'
import type { RowAction } from '../../../components/module/types'
import type { PurchaseOrder } from '../types/purchaseOrder'

export interface OrderHandlers {
  onView: (order: PurchaseOrder) => void
  onValidate: (order: PurchaseOrder) => void
  onReceive: (order: PurchaseOrder) => void
}

export const orderActions = (order: PurchaseOrder, handlers: OrderHandlers): RowAction[] => [
  { key: 'view', label: 'Voir le détail', icon: Eye, onSelect: () => handlers.onView(order) },
  ...(order.status === 'DRAFT' ? [{ key: 'validate', label: 'Valider la commande', icon: CheckCheck, onSelect: () => handlers.onValidate(order) }] : []),
  ...(order.status === 'VALIDATED' ? [{ key: 'receive', label: 'Réceptionner', icon: PackageCheck, onSelect: () => handlers.onReceive(order) }] : []),
]

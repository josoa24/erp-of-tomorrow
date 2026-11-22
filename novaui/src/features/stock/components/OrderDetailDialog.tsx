import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { StatusPill } from '../../../components/ui/status-pill'
import { formatOrderAmount, formatOrderDate, orderNumber, ORDER_STATUS, supplierName } from '../services/orderFormat'
import type { PurchaseOrder, Supplier } from '../types/purchaseOrder'

interface OrderDetailDialogProps {
  order: PurchaseOrder
  suppliers: Supplier[]
  onClose: () => void
}

export const OrderDetailDialog = ({ order, suppliers, onClose }: OrderDetailDialogProps) => (
  <Dialog
    open
    onClose={onClose}
    title={`Commande ${orderNumber(order)}`}
    description={`${supplierName(suppliers, order.supplierId)} · créée le ${formatOrderDate(order.createdAt)}`}
    footer={<Button variant="secondary" onClick={onClose}>Fermer</Button>}
  >
    <div className="flex flex-col gap-3">
      <StatusPill {...ORDER_STATUS[order.status]} />
      <ul className="flex flex-col divide-y divide-line-subtle">
        {order.lines.map((line) => (
          <li key={line.productId} className="flex items-center justify-between gap-3 py-2 text-body-s">
            <span className="text-ink-primary">{line.productName ?? `Produit #${line.productId}`}</span>
            <span className="text-ink-secondary">{line.quantity} × {formatOrderAmount(line.unitPrice)}</span>
          </li>
        ))}
      </ul>
      <p className="text-right text-body-strong text-ink-primary">Total : {formatOrderAmount(order.totalAmount)}</p>
      {order.validatedAt && <p className="text-micro text-ink-tertiary">Validée le {formatOrderDate(order.validatedAt)}</p>}
      {order.receivedAt && <p className="text-micro text-ink-tertiary">Réceptionnée le {formatOrderDate(order.receivedAt)}</p>}
    </div>
  </Dialog>
)

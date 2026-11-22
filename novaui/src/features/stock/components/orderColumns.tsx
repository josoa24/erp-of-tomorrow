import type { DataTableColumn } from '../../../components/module/types'
import { StatusPill } from '../../../components/ui/status-pill'
import { formatOrderAmount, formatOrderDate, linesSummary, orderNumber, ORDER_STATUS, supplierName } from '../services/orderFormat'
import type { PurchaseOrder, Supplier } from '../types/purchaseOrder'

export const orderColumns = (suppliers: Supplier[]): DataTableColumn<PurchaseOrder>[] => [
  { key: 'number', header: 'Commande', width: 140, render: (order) => <span className="text-label text-ink-primary">{orderNumber(order)}</span> },
  { key: 'supplier', header: 'Fournisseur', width: 260, render: (order) => supplierName(suppliers, order.supplierId) },
  { key: 'lines', header: 'Lignes', width: 120, render: linesSummary },
  { key: 'total', header: 'Montant', width: 160, align: 'end', render: (order) => formatOrderAmount(order.totalAmount) },
  { key: 'date', header: 'Créée le', width: 140, render: (order) => formatOrderDate(order.createdAt) },
  { key: 'status', header: 'Statut', width: 150, render: (order) => <StatusPill {...ORDER_STATUS[order.status]} /> },
]

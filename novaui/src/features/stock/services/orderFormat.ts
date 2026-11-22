import { CURRENCY_LABEL } from '../../../constants/appInfo'
import type { StatusTone } from '../../../components/ui/status-pill'
import type { OrderStatus, PurchaseOrder, Supplier } from '../types/purchaseOrder'

const amount = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 })
const day = new Intl.DateTimeFormat('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' })

export const ORDER_STATUS: Record<OrderStatus, { label: string; tone: StatusTone }> = {
  DRAFT: { label: 'Brouillon', tone: 'neutral' },
  VALIDATED: { label: 'Validée', tone: 'info' },
  RECEIVED: { label: 'Reçue', tone: 'success' },
}

export const formatOrderAmount = (value: number): string => `${amount.format(value)} ${CURRENCY_LABEL}`
export const formatOrderDate = (iso: string): string => day.format(new Date(iso))
export const orderNumber = (order: PurchaseOrder): string => `BC-${String(order.id).padStart(4, '0')}`
export const supplierName = (suppliers: Supplier[], id: number): string =>
  suppliers.find((supplier) => supplier.id === id)?.name ?? `Fournisseur #${id}`
export const linesSummary = (order: PurchaseOrder): string =>
  `${order.lines.length} ${order.lines.length > 1 ? 'lignes' : 'ligne'}`

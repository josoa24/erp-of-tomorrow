import { CheckCheck, FilePen, PackageCheck } from 'lucide-react'
import type { KpiItem } from '../../../components/module/types'
import type { OrderStatus, PurchaseOrder } from '../types/purchaseOrder'
import { formatOrderAmount } from './orderFormat'

const count = (orders: PurchaseOrder[], status: OrderStatus) => orders.filter((order) => order.status === status)
const sum = (orders: PurchaseOrder[]) => orders.reduce((total, order) => total + order.totalAmount, 0)

export const orderKpis = (orders: PurchaseOrder[]): KpiItem[] => [
  { key: 'draft', icon: FilePen, tone: 'lilac', label: 'À valider', value: count(orders, 'DRAFT').length, hint: 'brouillons' },
  { key: 'validated', icon: CheckCheck, tone: 'mint', label: 'À réceptionner', value: count(orders, 'VALIDATED').length, hint: formatOrderAmount(sum(count(orders, 'VALIDATED'))) },
  { key: 'received', icon: PackageCheck, tone: 'blush', label: 'Reçues', value: count(orders, 'RECEIVED').length, hint: 'commandes réceptionnées' },
]

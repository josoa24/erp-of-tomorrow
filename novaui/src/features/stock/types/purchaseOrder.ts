export type OrderStatus = 'DRAFT' | 'VALIDATED' | 'RECEIVED'

export interface PurchaseOrderLine {
  productId: number
  productName?: string | null
  quantity: number
  unitPrice: number
}

export interface PurchaseOrder {
  id: number
  supplierId: number
  status: OrderStatus
  totalAmount: number
  lines: PurchaseOrderLine[]
  createdAt: string
  validatedAt: string | null
  receivedAt: string | null
}

export interface PurchaseOrderRequest {
  supplierId: number
  lines: { productId: number; quantity: number; unitPrice: number }[]
}

export interface Supplier {
  id: number
  name: string
  city: string | null
  usualDeliveryDays: number | null
}

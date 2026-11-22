export type PaymentMethod = 'CASH' | 'MVOLA' | 'ORANGE_MONEY' | 'AIRTEL_MONEY' | 'CREDIT'
export type PaymentStatus = 'DRAFT' | 'TO_COLLECT' | 'OVERDUE' | 'PAID' | 'CANCELLED'
export type SaleStatus = 'DRAFT' | 'VALIDATED'

export interface SaleLine {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  lineTotal: number
}

export interface Sale {
  id: number
  number: string
  customerId: number | null
  customerName: string
  status: SaleStatus
  paymentStatus: PaymentStatus
  totalAmount: number
  paidAmount: number
  paymentMethod: PaymentMethod | null
  creditDays: number | null
  dueDate: string | null
  toDeliver: boolean
  lines: SaleLine[]
  createdAt: string
  validatedAt: string | null
}

export interface SaleLineRequest {
  productId: number
  quantity: number
}

export interface SaleRequest {
  customerId: number | null
  lines: SaleLineRequest[]
  toDeliver: boolean
  paymentMethod?: PaymentMethod
  creditDays?: number
}

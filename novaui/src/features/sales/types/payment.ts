import type { PaymentMethod } from './sale'

export type CollectMethod = Exclude<PaymentMethod, 'CREDIT'>

export interface SalePayment {
  id: number
  saleId: number
  amount: number
  method: PaymentMethod
  paidAt: string
  reference: string | null
  recordedBy: number | null
}

export interface PaymentRequest {
  amount: number
  method: CollectMethod
  reference?: string
}

export interface CancellationRequest {
  reason: string
}

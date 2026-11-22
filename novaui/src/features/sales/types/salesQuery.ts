import type { PaymentMethod, PaymentStatus } from './sale'

export type SalesFilterKey = 'toutes' | 'a-encaisser' | 'payees' | 'annulees' | 'a-credit'

export type SalesListQuery = {
  search?: string
  paymentStatus?: PaymentStatus[]
  paymentMethod?: PaymentMethod
  page: number
  size: number
}

export interface SalesSearch {
  filtre?: SalesFilterKey
  recherche?: string
  page?: number
}

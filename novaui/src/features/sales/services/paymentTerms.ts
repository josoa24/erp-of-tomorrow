import type { CollectMethod } from '../types/payment'
import type { PaymentMethod, Sale } from '../types/sale'

export type CreditDays = 15 | 30 | 45

export const CREDIT_DAY_OPTIONS: readonly CreditDays[] = [15, 30, 45]
export const DEFAULT_CREDIT_DAYS: CreditDays = 30

export const PAYMENT_METHOD_LABELS: Readonly<Record<PaymentMethod, string>> = {
  CASH: 'Espèces',
  MVOLA: 'MVola',
  ORANGE_MONEY: 'Orange Money',
  AIRTEL_MONEY: 'Airtel Money',
  CREDIT: 'Crédit',
}

export const COLLECT_METHODS: readonly CollectMethod[] = ['CASH', 'MVOLA', 'ORANGE_MONEY', 'AIRTEL_MONEY']
export const SALE_PAYMENT_METHODS: readonly PaymentMethod[] = [...COLLECT_METHODS, 'CREDIT']

export const isCreditDays = (value: number | null): value is CreditDays =>
  CREDIT_DAY_OPTIONS.some((option) => option === value)

export const isCollectMethod = (value: PaymentMethod | null): value is CollectMethod =>
  COLLECT_METHODS.some((method) => method === value)

export const paymentTermsLabel = ({
  paymentMethod,
  creditDays,
}: Pick<Sale, 'paymentMethod' | 'creditDays'>): string | null => {
  if (paymentMethod === null) return null
  if (paymentMethod === 'CREDIT' && creditDays) return `Crédit · ${creditDays} jours`
  return PAYMENT_METHOD_LABELS[paymentMethod]
}

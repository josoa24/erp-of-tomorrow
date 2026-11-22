import type { CollectMethod } from '../types/payment'
import type { Sale } from '../types/sale'
import { isCollectMethod } from './paymentTerms'
import { remainingAmount } from './saleActions'

export const parseAmount = (text: string): number => {
  const normalized = text.replace(/\s/g, '').replace(',', '.')
  return normalized === '' ? Number.NaN : Number(normalized)
}

export const isCollectAmountValid = (amount: number, remaining: number): boolean =>
  Number.isFinite(amount) && amount > 0 && amount <= remaining && Math.abs(Math.round(amount * 100) - amount * 100) < 1e-6

export const defaultCollectMethod = (sale: Pick<Sale, 'paymentMethod'>): CollectMethod =>
  isCollectMethod(sale.paymentMethod) ? sale.paymentMethod : 'CASH'

export const defaultCollectAmount = (sale: Pick<Sale, 'totalAmount' | 'paidAmount'>): string =>
  String(remainingAmount(sale))

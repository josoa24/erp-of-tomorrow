import type { Sale } from '../types/sale'

export type SaleActionKey = 'edit' | 'validate' | 'delete' | 'collect' | 'cancel' | 'detail'

export const remainingAmount = ({ totalAmount, paidAmount }: Pick<Sale, 'totalAmount' | 'paidAmount'>): number =>
  Math.max(totalAmount - paidAmount, 0)

export const saleActionKeys = (sale: Sale): SaleActionKey[] => {
  switch (sale.paymentStatus) {
    case 'DRAFT':
      return ['edit', 'validate', 'delete']
    case 'TO_COLLECT':
    case 'OVERDUE':
      return sale.paidAmount > 0 ? ['collect', 'detail'] : ['collect', 'cancel', 'detail']
    default:
      return ['detail']
  }
}

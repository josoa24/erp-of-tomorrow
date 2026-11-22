import type { Sale, SaleRequest } from '../types/sale'
import { DEFAULT_CREDIT_DAYS, isCreditDays } from './paymentTerms'
import type { SaleDraft } from './saleDraft'

const paymentTerms = (draft: SaleDraft): Pick<SaleRequest, 'paymentMethod' | 'creditDays'> =>
  draft.paymentMethod === 'CREDIT'
    ? { paymentMethod: 'CREDIT', creditDays: draft.creditDays }
    : { paymentMethod: draft.paymentMethod }

export const toSaleRequest = (draft: SaleDraft, withPaymentTerms: boolean): SaleRequest => ({
  customerId: draft.customer?.id ?? null,
  lines: draft.lines.map(({ productId, quantity }) => ({ productId, quantity })),
  toDeliver: draft.toDeliver,
  ...(withPaymentTerms && paymentTerms(draft)),
})

export const draftFromSale = (sale: Sale): SaleDraft => ({
  customer: sale.customerId === null ? null : { id: sale.customerId, name: sale.customerName, city: null, phone: null },
  lines: sale.lines.map((line) => ({
    productId: line.productId,
    productName: line.productName,
    unitPrice: line.unitPrice,
    available: null,
    quantity: line.quantity,
  })),
  toDeliver: sale.toDeliver,
  paymentMethod: sale.paymentMethod ?? 'CASH',
  creditDays: isCreditDays(sale.creditDays) ? sale.creditDays : DEFAULT_CREDIT_DAYS,
})

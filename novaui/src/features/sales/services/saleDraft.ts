import type { CatalogProduct, CustomerOption } from '../types/catalog'
import type { PaymentMethod } from '../types/sale'
import { DEFAULT_CREDIT_DAYS, type CreditDays } from './paymentTerms'

export interface DraftLine {
  productId: number
  productName: string
  unitPrice: number
  available: number | null
  quantity: number
}

export interface SaleDraft {
  customer: CustomerOption | null
  lines: DraftLine[]
  toDeliver: boolean
  paymentMethod: PaymentMethod
  creditDays: CreditDays
}

export const EMPTY_DRAFT: SaleDraft = {
  customer: null,
  lines: [],
  toDeliver: false,
  paymentMethod: 'CASH',
  creditDays: DEFAULT_CREDIT_DAYS,
}

const toDraftLine = (product: CatalogProduct): DraftLine => ({
  productId: product.id,
  productName: product.name,
  unitPrice: product.unitPrice,
  available: product.quantity,
  quantity: 1,
})

const updateLines = (draft: SaleDraft, lines: DraftLine[]): SaleDraft => ({ ...draft, lines })

export const addProduct = (draft: SaleDraft, product: CatalogProduct): SaleDraft => {
  const existing = draft.lines.some((line) => line.productId === product.id)
  if (!existing) return updateLines(draft, [...draft.lines, toDraftLine(product)])
  return updateLines(
    draft,
    draft.lines.map((line) => (line.productId === product.id ? { ...line, quantity: line.quantity + 1 } : line)),
  )
}

export const setLineQuantity = (draft: SaleDraft, productId: number, quantity: number): SaleDraft =>
  updateLines(
    draft,
    draft.lines.map((line) => (line.productId === productId ? { ...line, quantity } : line)),
  )

export const removeLine = (draft: SaleDraft, productId: number): SaleDraft =>
  updateLines(
    draft,
    draft.lines.filter((line) => line.productId !== productId),
  )

export const isQuantityValid = (quantity: number): boolean => Number.isInteger(quantity) && quantity >= 1

export const exceedsStock = (line: DraftLine): boolean => line.available !== null && line.quantity > line.available

export const lineTotal = (line: DraftLine): number => (isQuantityValid(line.quantity) ? line.unitPrice * line.quantity : 0)

export const draftTotal = (draft: SaleDraft): number => draft.lines.reduce((sum, line) => sum + lineTotal(line), 0)

export const canSaveDraft = (draft: SaleDraft): boolean =>
  draft.lines.length > 0 && draft.lines.every((line) => isQuantityValid(line.quantity))

export const canValidateDraft = (draft: SaleDraft): boolean => canSaveDraft(draft) && !draft.lines.some(exceedsStock)

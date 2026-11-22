import { useState } from 'react'
import { addProduct, removeLine, setLineQuantity, type SaleDraft } from '../services/saleDraft'
import type { CatalogProduct, CustomerOption } from '../types/catalog'
import type { PaymentMethod } from '../types/sale'
import type { CreditDays } from '../services/paymentTerms'

export const useSaleDraftState = (initial: SaleDraft) => {
  const [draft, setDraft] = useState(initial)

  return {
    draft,
    setCustomer: (customer: CustomerOption | null) =>
      setDraft((current) => ({
        ...current,
        customer,
        paymentMethod: customer === null && current.paymentMethod === 'CREDIT' ? 'CASH' : current.paymentMethod,
      })),
    addProduct: (product: CatalogProduct) => setDraft((current) => addProduct(current, product)),
    setQuantity: (productId: number, quantity: number) =>
      setDraft((current) => setLineQuantity(current, productId, quantity)),
    removeLine: (productId: number) => setDraft((current) => removeLine(current, productId)),
    setToDeliver: (toDeliver: boolean) => setDraft((current) => ({ ...current, toDeliver })),
    setMethod: (paymentMethod: PaymentMethod) => setDraft((current) => ({ ...current, paymentMethod })),
    setCreditDays: (creditDays: CreditDays) => setDraft((current) => ({ ...current, creditDays })),
  }
}

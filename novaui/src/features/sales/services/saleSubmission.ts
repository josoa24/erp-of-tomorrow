import { createSale, updateSale, validateSale } from '../api/salesApi'
import type { Sale, SaleRequest } from '../types/sale'

export interface SaleSubmission {
  saleId: number | null
  request: SaleRequest
  validate: boolean
}

export const submitSale = async ({ saleId, request, validate }: SaleSubmission): Promise<Sale> => {
  const saved = saleId === null ? await createSale(request) : await updateSale(saleId, request)
  return validate ? validateSale(saved.id) : saved
}

export const submissionMessage = (sale: Sale, { validate }: Pick<SaleSubmission, 'validate'>): string =>
  validate ? `Vente ${sale.number} validée` : `Brouillon ${sale.number} enregistré`

import { deleteSale, validateSale } from '../api/salesApi'
import { cancelSale } from '../api/saleCancellationApi'
import { recordSalePayment } from '../api/salePaymentsApi'
import { submissionMessage, submitSale } from '../services/saleSubmission'
import { formatAmount } from '../services/salesFormat'
import type { PaymentRequest } from '../types/payment'
import type { Sale } from '../types/sale'
import { useSalesMutation } from './useSalesMutation'

export const useSubmitSale = () => useSalesMutation({ run: submitSale, message: submissionMessage })

export const useValidateDraft = () =>
  useSalesMutation({
    run: (sale: Sale) => validateSale(sale.id),
    message: (validated) => `Vente ${validated.number} validée`,
  })

export const useDeleteDraft = () =>
  useSalesMutation({
    run: (sale: Sale) => deleteSale(sale.id),
    message: (_, sale) => `Brouillon ${sale.number} supprimé`,
  })

export const useRecordPayment = () =>
  useSalesMutation({
    run: ({ sale, request }: { sale: Sale; request: PaymentRequest }) => recordSalePayment(sale.id, request),
    message: (payment, { sale }) => `Encaissement de ${formatAmount(payment.amount)} enregistré pour ${sale.number}`,
  })

export const useCancelSale = () =>
  useSalesMutation({
    run: ({ sale, reason }: { sale: Sale; reason: string }) => cancelSale(sale.id, { reason }),
    message: (cancelled) => `Vente ${cancelled.number} annulée, stock réintégré`,
  })

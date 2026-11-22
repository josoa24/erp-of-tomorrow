import { useQuery } from '@tanstack/react-query'
import { listSalePayments } from '../api/salePaymentsApi'
import type { SalePayment } from '../types/payment'
import { salesQueryKeys } from './salesQueryKeys'
import { useRedirectOnError } from './useRedirectOnError'

const NONE: SalePayment[] = []

export const useSalePayments = (saleId: number, enabled: boolean) => {
  const result = useQuery({
    queryKey: salesQueryKeys.payments(saleId),
    queryFn: ({ signal }) => listSalePayments(saleId, { signal }),
    enabled,
  })
  useRedirectOnError(result.error)

  return { payments: result.data ?? NONE, loading: enabled && result.isPending }
}

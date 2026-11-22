import { httpPost } from '../../../services/api/httpClient'
import type { CancellationRequest } from '../types/payment'
import type { Sale } from '../types/sale'
import { saleUrl } from './salesApi'

export const cancelSale = (saleId: number, request: CancellationRequest): Promise<Sale> =>
  httpPost<Sale>(`${saleUrl(saleId)}/cancel`, request)

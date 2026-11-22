import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import type { PaymentRequest, SalePayment } from '../types/payment'
import { saleUrl } from './salesApi'

export const listSalePayments = (saleId: number, options: RequestOptions = {}): Promise<SalePayment[]> =>
  httpGet<SalePayment[]>(`${saleUrl(saleId)}/payments`, options)

export const recordSalePayment = (saleId: number, request: PaymentRequest): Promise<SalePayment> =>
  httpPost<SalePayment>(`${saleUrl(saleId)}/payments`, request)

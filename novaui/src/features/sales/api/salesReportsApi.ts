import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import type { CreditSummary, SaleCounts, SalesKpis } from '../types/salesReport'
import { SALES_PATH } from './salesApi'

export const getSaleCounts = (options: RequestOptions = {}): Promise<SaleCounts> =>
  httpGet<SaleCounts>(`${SALES_PATH}/counts`, options)

export const getSalesKpis = (options: RequestOptions = {}): Promise<SalesKpis> =>
  httpGet<SalesKpis>(`${SALES_PATH}/kpis`, options)

export const getCreditSummary = (options: RequestOptions = {}): Promise<CreditSummary> =>
  httpGet<CreditSummary>(`${SALES_PATH}/credit-summary`, options)

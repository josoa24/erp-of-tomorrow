import type { DateRange } from '../../types/dashboard'
import type { SalesSummary } from '../../types/sales'
import { httpGet } from './httpClient'

export const getSalesSummary = ({ from, to }: DateRange): Promise<SalesSummary> =>
  httpGet<SalesSummary>(`/api/vente/sales/summary?from=${from}&to=${to}`)

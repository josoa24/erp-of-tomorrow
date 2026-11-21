import type { StockStatusItem, StockSummary } from '../../types/stock'
import { httpGet } from './httpClient'

export const getStockSummary = (): Promise<StockSummary> =>
  httpGet<StockSummary>('/api/stock/summary')

export const getStockStatus = (): Promise<StockStatusItem[]> =>
  httpGet<StockStatusItem[]>('/api/stock/status')

import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import type { StockSummary } from '../types/stock'

export const STOCK_SUMMARY_PATH = '/api/stock/summary'

export const getStockSummary = (options: RequestOptions = {}): Promise<StockSummary> =>
  httpGet<StockSummary>(STOCK_SUMMARY_PATH, options)

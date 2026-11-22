import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { StockCounts, StockStatusItem } from '../types/stock'

export const STOCK_STATUS_PATH = '/api/stock/status'
export const STOCK_COUNTS_PATH = `${STOCK_STATUS_PATH}/counts`

export const getStockStatus = (search: string, options: RequestOptions = {}): Promise<StockStatusItem[]> =>
  httpGet<StockStatusItem[]>(`${STOCK_STATUS_PATH}${buildQuery({ search })}`, options)

export const getStockCounts = (options: RequestOptions = {}): Promise<StockCounts> =>
  httpGet<StockCounts>(STOCK_COUNTS_PATH, options)

import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { StockProduct } from '../types/stock'

export const STOCK_PRODUCTS_PATH = '/api/stock/products'

export const productPath = (productId: number): string => `${STOCK_PRODUCTS_PATH}/${productId}`

export const searchStockProducts = (search: string, options: RequestOptions = {}): Promise<StockProduct[]> =>
  httpGet<StockProduct[]>(`${STOCK_PRODUCTS_PATH}${buildQuery({ search })}`, options)

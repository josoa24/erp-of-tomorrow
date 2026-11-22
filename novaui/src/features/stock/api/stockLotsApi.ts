import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import type { StockLot } from '../types/stock'
import { productPath } from './stockProductsApi'

export const getProductLots = (productId: number, options: RequestOptions = {}): Promise<StockLot[]> =>
  httpGet<StockLot[]>(`${productPath(productId)}/lots`, options)

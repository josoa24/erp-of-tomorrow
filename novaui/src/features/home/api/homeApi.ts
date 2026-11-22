import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import { productPath } from '../../stock/api/stockProductsApi'
import type { DraftOrderRequest, EclipseSuggestion, ProductPrices, Replenishment, SalesKpis } from '../types/home'

export const getSalesKpis = (options: RequestOptions = {}): Promise<SalesKpis> =>
  httpGet<SalesKpis>('/api/vente/sales/kpis', options)

export const getReplenishment = (options: RequestOptions = {}): Promise<Replenishment[]> =>
  httpGet<Replenishment[]>('/api/stock/replenishment?onlyNeeded=true', options)

export const getProductPrices = (productId: number): Promise<ProductPrices> =>
  httpGet<ProductPrices>(productPath(productId))

export const getEclipseSuggestions = (options: RequestOptions = {}): Promise<EclipseSuggestion[]> =>
  httpGet<EclipseSuggestion[]>('/api/ai/eclipse/suggestions', options)

export const createDraftOrder = (request: DraftOrderRequest): Promise<{ id: number }> =>
  httpPost<{ id: number }>('/api/stock/purchase-orders', request)

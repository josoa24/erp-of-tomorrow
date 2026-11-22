import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import { productPath } from '../../stock/api/stockProductsApi'
import type { DraftOrderRequest, EclipseSuggestion, ModuleIndicator, ProductPrices, Replenishment, SalesKpis } from '../types/home'

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

export const getModuleIndicators = (slug: string, options: RequestOptions = {}): Promise<ModuleIndicator[]> =>
  httpGet<ModuleIndicator[]>(`/api/modules/definitions/${encodeURIComponent(slug)}/indicators`, options)

export const getModuleSummaries = (options: RequestOptions = {}): Promise<{ slug: string; name: string }[]> =>
  httpGet<{ slug: string; name: string }[]>('/api/modules/definitions', options)

export const getModuleDefinition = (slug: string, options: RequestOptions = {}): Promise<{ indicators: { kind: string }[] }> =>
  httpGet<{ indicators: { kind: string }[] }>(`/api/modules/definitions/${encodeURIComponent(slug)}`, options)

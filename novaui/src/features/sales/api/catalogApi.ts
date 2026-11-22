import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { CatalogProduct } from '../types/catalog'

export const CATALOG_PRODUCTS_PATH = '/api/vente/catalog/products'

export const searchCatalogProducts = (search: string, options: RequestOptions = {}): Promise<CatalogProduct[]> =>
  httpGet<CatalogProduct[]>(`${CATALOG_PRODUCTS_PATH}${buildQuery({ search })}`, options)

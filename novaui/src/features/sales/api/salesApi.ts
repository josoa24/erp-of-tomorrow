import {
  httpDelete,
  httpGet,
  httpGetPage,
  httpPost,
  httpPut,
  type Page,
  type RequestOptions,
} from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { Sale, SaleRequest } from '../types/sale'
import type { SalesListQuery } from '../types/salesQuery'

export const SALES_PATH = '/api/vente/sales'

export const saleUrl = (id: number): string => `${SALES_PATH}/${id}`

export const listSales = (query: SalesListQuery, options: RequestOptions = {}): Promise<Page<Sale>> =>
  httpGetPage<Sale>(`${SALES_PATH}${buildQuery(query)}`, options)

export const getSale = (id: number, options: RequestOptions = {}): Promise<Sale> =>
  httpGet<Sale>(saleUrl(id), options)

export const createSale = (request: SaleRequest): Promise<Sale> => httpPost<Sale>(SALES_PATH, request)

export const updateSale = (id: number, request: SaleRequest): Promise<Sale> => httpPut<Sale>(saleUrl(id), request)

export const deleteSale = (id: number): Promise<void> => httpDelete(saleUrl(id))

export const validateSale = (id: number): Promise<Sale> => httpPost<Sale>(`${saleUrl(id)}/validate`)

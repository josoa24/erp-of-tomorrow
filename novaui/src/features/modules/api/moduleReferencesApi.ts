import { httpGet, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { ReferenceTarget } from '../types/moduleDefinition'

export const REFERENCE_PATHS: Record<ReferenceTarget, string> = {
  CUSTOMER: '/api/vente/customers',
  SALE: '/api/vente/sales',
  EMPLOYEE: '/api/rh/employees',
  PRODUCT: '/api/stock/products',
  SUPPLIER: '/api/stock/suppliers',
}

export const searchReference = (
  target: ReferenceTarget,
  search: string,
  options: RequestOptions = {},
): Promise<Record<string, unknown>[]> =>
  httpGet<Record<string, unknown>[]>(`${REFERENCE_PATHS[target]}${buildQuery({ search })}`, options)

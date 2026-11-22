import { httpGetPage, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { CustomerOption } from '../types/catalog'

export const CUSTOMERS_PATH = '/api/vente/customers'
export const CUSTOMER_OPTIONS_SIZE = 6

export const searchCustomerOptions = async (
  search: string,
  options: RequestOptions = {},
): Promise<CustomerOption[]> => {
  const query = buildQuery({ search, page: 0, size: CUSTOMER_OPTIONS_SIZE })
  const page = await httpGetPage<CustomerOption>(`${CUSTOMERS_PATH}${query}`, options)
  return page.items
}

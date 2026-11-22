export const STOCK_QUERY_KEY = ['stock'] as const

export const stockQueryKeys = {
  status: (search: string) => [...STOCK_QUERY_KEY, 'status', search] as const,
  counts: () => [...STOCK_QUERY_KEY, 'counts'] as const,
  summary: () => [...STOCK_QUERY_KEY, 'summary'] as const,
  products: (search: string) => [...STOCK_QUERY_KEY, 'products', search] as const,
  lots: (productId: number) => [...STOCK_QUERY_KEY, 'lots', productId] as const,
}

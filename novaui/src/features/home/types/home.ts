export interface SalesKpis {
  todayRevenue: number
  todaySalesCount: number
  sameWeekdayLastWeekRevenue: number
}

export interface Replenishment {
  productId: number
  productName: string
  suggestedSupplierId: number | null
  recommendedQuantity: number | null
  coverageDays: number | null
}

export interface ProductPrices {
  unitPrice: number
  purchasePrice: number | null
}

export interface EclipseSuggestion {
  adaptationKey: string
  moduleKey: string
  title: string
  reason: string
}

export interface DraftOrderRequest {
  supplierId: number
  lines: { productId: number; quantity: number; unitPrice: number }[]
}

export interface ModuleIndicator {
  key: string
  label: string
  kind: 'COUNT' | 'SUM' | 'RATIO_TODAY'
  value: number
  total?: number
  detail: string[]
}

export interface DeliveryIndicator {
  moduleName: string
  delivered: number
  total: number
  inProgress: { label: string; value: number } | null
}

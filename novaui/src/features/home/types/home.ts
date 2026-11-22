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

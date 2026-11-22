export type StockRisk = 'OK' | 'WARNING' | 'CRITICAL'

export interface StockSummary {
  totalProducts: number
  lowStockProducts: number
  totalStockValue: number
}

export interface StockStatusItem {
  productId: number
  productName: string
  quantity: number
  minThreshold: number
  daysRemaining: number | null
  risk: StockRisk
}

export type StockRisk = 'OK' | 'WARNING' | 'CRITICAL'

export type StockStatusLabel = 'RUPTURE_RISK' | 'BELOW_THRESHOLD' | 'IN_STOCK'

export type MovementType = 'IN' | 'OUT'

export interface StockStatusItem {
  productId: number
  productName: string
  sku: string
  unit: string
  category: string | null
  quantity: number
  minThreshold: number
  lowStock: boolean
  risk: StockRisk
  statusLabel: StockStatusLabel
  lotNumber: string | null
  expiryDate: string | null
  daysToExpiry: number | null
  expiringSoon: boolean
}

export interface StockSummary {
  totalProducts: number
  lowStockProducts: number
  totalStockValue: number
  availabilityRate: number
  criticalLowStock: number
  totalPurchaseValue: number
  productsWithoutPurchasePrice: number
  expiringSoon: number
}

export interface StockCounts {
  all: number
  alert: number
  ruptureRisk: number
  expiringSoon: number
}

export interface StockProduct {
  id: number
  name: string
  sku: string
  unit: string
  quantity: number
  minThreshold: number
  category: string | null
}

export interface MovementRequest {
  type: MovementType
  quantity: number
  reason?: string
  lotNumber?: string
  expiryDate?: string
}

export interface MovementResponse {
  id: number
  productId: number
  type: MovementType
  quantity: number
  stockAfter: number
}

export interface StockLot {
  lotNumber: string
  expiryDate: string
  quantity: number
  remaining: number
  receivedAt: string
}

import type { StockCounts, StockLot, StockStatusItem, StockSummary } from '../types/stock'

export const makeStatusItem = (overrides: Partial<StockStatusItem> = {}): StockStatusItem => ({
  productId: 4,
  productName: 'Riz blanc 25 kg',
  sku: 'RIZ-25',
  unit: 'sac',
  category: 'Épicerie sèche',
  quantity: 40,
  minThreshold: 10,
  lowStock: false,
  risk: 'OK',
  statusLabel: 'IN_STOCK',
  lotNumber: 'LOT-2611-A',
  expiryDate: '2027-02-12',
  daysToExpiry: 82,
  expiringSoon: false,
  ...overrides,
})

export const RUPTURE_ITEM = makeStatusItem({
  productId: 1,
  productName: 'Bougies (paquet de 6)',
  sku: 'BOUG-6',
  unit: 'unité',
  category: 'Maison',
  quantity: 4,
  lowStock: true,
  risk: 'CRITICAL',
  statusLabel: 'RUPTURE_RISK',
  lotNumber: null,
  expiryDate: null,
  daysToExpiry: null,
})

export const EXPIRING_ITEM = makeStatusItem({
  productId: 3,
  productName: 'Lait concentré 397 g',
  sku: 'LAIT-397',
  unit: 'u.',
  category: null,
  quantity: 96,
  minThreshold: 24,
  lotNumber: 'LOT-2609-F',
  expiryDate: '2026-12-17',
  daysToExpiry: 23,
  expiringSoon: true,
})

export const STOCK_SUMMARY: StockSummary = {
  totalProducts: 64,
  lowStockProducts: 5,
  totalStockValue: 51_000_000,
  availabilityRate: 0.922,
  criticalLowStock: 2,
  totalPurchaseValue: 42_600_000,
  productsWithoutPurchasePrice: 0,
  expiringSoon: 3,
}

export const STOCK_COUNTS: StockCounts = { all: 64, alert: 5, ruptureRisk: 2, expiringSoon: 3 }

export const RIZ_LOT: StockLot = {
  lotNumber: 'LOT-2611-A',
  expiryDate: '2027-02-12',
  quantity: 50,
  remaining: 40,
  receivedAt: '2026-11-02T07:30:00Z',
}

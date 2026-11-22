import type { StockStatusItem } from '../../types/stock'
import { expiryLabel, stockStatusView } from '../stockStatus'

const item = (overrides: Partial<StockStatusItem>): StockStatusItem => ({
  productId: 1,
  productName: 'Lait concentré 397 g',
  sku: 'LAIT-397',
  unit: 'u.',
  category: 'Produits laitiers',
  quantity: 96,
  minThreshold: 24,
  lowStock: false,
  risk: 'OK',
  statusLabel: 'IN_STOCK',
  lotNumber: 'LOT-2609-F',
  expiryDate: '2026-12-17',
  daysToExpiry: 23,
  expiringSoon: true,
  ...overrides,
})

describe('stockStatusView', () => {
  it('shouldMapServerLabels_whenGeneric', () => {
    expect(stockStatusView(item({ statusLabel: 'RUPTURE_RISK' }), false)).toEqual({
      label: 'Risque de rupture',
      tone: 'critical',
    })
    expect(stockStatusView(item({ statusLabel: 'BELOW_THRESHOLD' }), false).label).toBe('Sous le seuil')
    expect(stockStatusView(item({}), false)).toEqual({ label: 'En stock', tone: 'success' })
  })

  it('shouldShowExpiry_whenAdaptedAndExpiringSoon', () => {
    expect(stockStatusView(item({}), true)).toEqual({ label: 'Expire dans 23 j', tone: 'critical' })
  })

  it('shouldKeepRupture_whenRuptureAndExpiring', () => {
    expect(stockStatusView(item({ statusLabel: 'RUPTURE_RISK' }), true).label).toBe('Risque de rupture')
  })

  it('shouldKeepStockStatus_whenNotExpiringSoon', () => {
    expect(stockStatusView(item({ expiringSoon: false, daysToExpiry: 80 }), true).label).toBe('En stock')
  })
})

describe('expiryLabel', () => {
  it('shouldDescribeTodayAndPastExpiry', () => {
    expect(expiryLabel(0)).toBe('Expire aujourd’hui')
    expect(expiryLabel(-2)).toBe('Expiré')
  })
})

import type { StockCounts, StockStatusItem } from '../types/stock'

export const STOCK_FILTERS = ['tous', 'alerte', 'rupture', 'expire-bientot'] as const

export type StockFilter = (typeof STOCK_FILTERS)[number]

export const EXPIRY_FILTER: StockFilter = 'expire-bientot'

export interface StockFilterChip {
  key: StockFilter
  label: string
  count: number | undefined
  adapted: boolean
}

const LABELS: Record<StockFilter, string> = {
  tous: 'Tous',
  alerte: 'En alerte',
  rupture: 'Rupture',
  'expire-bientot': 'Expire bientôt',
}

const COUNT_KEYS: Record<StockFilter, keyof StockCounts> = {
  tous: 'all',
  alerte: 'alert',
  rupture: 'ruptureRisk',
  'expire-bientot': 'expiringSoon',
}

const MATCHERS: Record<StockFilter, (item: StockStatusItem) => boolean> = {
  tous: () => true,
  alerte: (item) => item.lowStock,
  rupture: (item) => item.statusLabel === 'RUPTURE_RISK',
  'expire-bientot': (item) => item.expiringSoon,
}

export const isStockFilter = (value: unknown): value is StockFilter =>
  STOCK_FILTERS.some((filter) => filter === value)

export const effectiveFilter = (filter: StockFilter, withExpiry: boolean): StockFilter =>
  filter === EXPIRY_FILTER && !withExpiry ? 'tous' : filter

export const filterStockItems = (items: StockStatusItem[], filter: StockFilter): StockStatusItem[] =>
  items.filter(MATCHERS[filter])

export const stockFilterChips = (counts: StockCounts | undefined, withExpiry: boolean): StockFilterChip[] =>
  STOCK_FILTERS.filter((key) => withExpiry || key !== EXPIRY_FILTER).map((key) => ({
    key,
    label: LABELS[key],
    count: counts?.[COUNT_KEYS[key]],
    adapted: key === EXPIRY_FILTER,
  }))

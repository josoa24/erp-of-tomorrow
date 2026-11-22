import type { StatusTone } from '../../../components/ui/status-pill'
import type { StockStatusItem, StockStatusLabel } from '../types/stock'

export interface StockStatusView {
  label: string
  tone: StatusTone
}

const BASE_STATUS: Record<StockStatusLabel, StockStatusView> = {
  RUPTURE_RISK: { label: 'Risque de rupture', tone: 'critical' },
  BELOW_THRESHOLD: { label: 'Sous le seuil', tone: 'warning' },
  IN_STOCK: { label: 'En stock', tone: 'success' },
}

export const expiryLabel = (daysToExpiry: number): string => {
  if (daysToExpiry < 0) return 'Expiré'
  if (daysToExpiry === 0) return 'Expire aujourd’hui'
  return `Expire dans ${daysToExpiry} j`
}

const showsExpiry = (item: StockStatusItem): item is StockStatusItem & { daysToExpiry: number } =>
  item.expiringSoon && item.daysToExpiry !== null && item.statusLabel !== 'RUPTURE_RISK'

export const stockStatusView = (item: StockStatusItem, withExpiry: boolean): StockStatusView =>
  withExpiry && showsExpiry(item)
    ? { label: expiryLabel(item.daysToExpiry), tone: 'critical' }
    : BASE_STATUS[item.statusLabel]

import { StatusPill } from '../../../components/ui/status-pill'
import { stockStatusView } from '../services/stockStatus'
import type { StockStatusItem } from '../types/stock'

interface StockStatusPillProps {
  item: StockStatusItem
  withExpiry: boolean
}

export const StockStatusPill = ({ item, withExpiry }: StockStatusPillProps) => {
  const { label, tone } = stockStatusView(item, withExpiry)
  return <StatusPill label={label} tone={tone} />
}

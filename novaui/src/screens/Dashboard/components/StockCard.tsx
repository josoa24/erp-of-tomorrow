import { Box } from 'lucide-react'
import { MetricCard } from '../../../components/common/MetricCard'
import { TrendBadge } from '../../../components/common/TrendBadge'
import type { StockCardData } from '../../../types/dashboard'
import { formatDecimal } from '../../../utils/format'
import { LockedCard } from './LockedCard'

interface StockCardProps {
  data: StockCardData | null
  loading: boolean
}

const rateValue = (rate: number | null) =>
  rate === null ? (
    '—'
  ) : (
    <>
      {formatDecimal(rate)}
      <span className="ml-0.5 text-[0.72em] font-medium">%</span>
    </>
  )

export const StockCard = ({ data, loading }: StockCardProps) => {
  if (!loading && !data) return <LockedCard icon={Box} label="Stock" />

  return (
    <MetricCard
      icon={Box}
      label="Stock"
      value={data ? rateValue(data.availabilityRate) : '…'}
      caption="Disponibilité"
      badge={
        data &&
        (data.lowStock === 0 ? (
          <TrendBadge tone="up">Aucune alerte</TrendBadge>
        ) : (
          <TrendBadge tone="warning">{data.lowStock} en alerte</TrendBadge>
        ))
      }
    />
  )
}

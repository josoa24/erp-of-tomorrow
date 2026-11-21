import { ShoppingCart } from 'lucide-react'
import { MetricCard } from '../../../components/common/MetricCard'
import { Sparkline } from '../../../components/common/Sparkline'
import { TrendBadge } from '../../../components/common/TrendBadge'
import type { SalesCardData } from '../../../types/dashboard'
import { formatCompactAmount, formatSignedPercent, pluralize } from '../../../utils/format'
import { LockedCard } from './LockedCard'

interface SalesCardProps {
  data: SalesCardData | null
  loading: boolean
}

const salesBadge = ({ trend, salesCount }: SalesCardData) =>
  trend === null ? (
    <TrendBadge tone="neutral">{pluralize(salesCount, 'vente', 'ventes')}</TrendBadge>
  ) : (
    <TrendBadge tone={trend >= 0 ? 'up' : 'down'}>{formatSignedPercent(trend)}</TrendBadge>
  )

export const SalesCard = ({ data, loading }: SalesCardProps) => {
  if (!loading && !data) return <LockedCard icon={ShoppingCart} label="Ventes" />

  return (
    <MetricCard
      icon={ShoppingCart}
      label="Ventes"
      value={data ? formatCompactAmount(data.revenue) : '…'}
      badge={data && salesBadge(data)}
      chart={data && <Sparkline values={data.daily} label="Évolution du chiffre d'affaires" />}
    />
  )
}

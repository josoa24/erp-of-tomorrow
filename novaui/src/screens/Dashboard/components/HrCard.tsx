import { Users } from 'lucide-react'
import { MetricCard } from '../../../components/common/MetricCard'
import { TrendBadge } from '../../../components/common/TrendBadge'
import type { HrCardData } from '../../../types/dashboard'
import { LockedCard } from './LockedCard'

interface HrCardProps {
  data: HrCardData | null
  loading: boolean
}

export const HrCard = ({ data, loading }: HrCardProps) => {
  if (!loading && !data) return <LockedCard icon={Users} label="Équipe" />

  return (
    <MetricCard
      icon={Users}
      label="Équipe"
      value={data ? data.totalEmployees : '…'}
      caption="Collaborateurs"
      badge={data && <TrendBadge tone="neutral">{data.onLeaveToday} en congé</TrendBadge>}
    />
  )
}

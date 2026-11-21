import type { LucideIcon } from 'lucide-react'
import { MetricCard } from '../../../components/common/MetricCard'
import { TrendBadge } from '../../../components/common/TrendBadge'

interface LockedCardProps {
  icon: LucideIcon
  label: string
}

export const LockedCard = ({ icon, label }: LockedCardProps) => (
  <MetricCard
    icon={icon}
    label={label}
    value="—"
    badge={<TrendBadge tone="neutral">Accès réservé</TrendBadge>}
  />
)

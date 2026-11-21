import type { LucideIcon } from 'lucide-react'
import type { ReactNode } from 'react'
import { GlassCard } from './GlassCard'
import styles from './MetricCard.module.css'

interface MetricCardProps {
  icon: LucideIcon
  label: string
  value: ReactNode
  caption?: string
  badge?: ReactNode
  chart?: ReactNode
}

export const MetricCard = ({ icon: Icon, label, value, caption, badge, chart }: MetricCardProps) => (
  <GlassCard className={styles.card}>
    <span className={styles.iconTile}>
      <Icon size={34} strokeWidth={1.4} aria-hidden="true" />
    </span>
    <div className={styles.body}>
      <h2 className={styles.label}>{label}</h2>
      <p className={styles.value}>{value}</p>
      {(caption || badge) && (
        <div className={styles.footer}>
          {caption && <span className={styles.caption}>{caption}</span>}
          {badge}
        </div>
      )}
    </div>
    {chart && <div className={styles.chart}>{chart}</div>}
  </GlassCard>
)

import type { ReactNode } from 'react'
import styles from './TrendBadge.module.css'

export type BadgeTone = 'up' | 'down' | 'warning' | 'neutral'

const ARROWS: Partial<Record<BadgeTone, string>> = { up: '▲', down: '▼' }

interface TrendBadgeProps {
  tone: BadgeTone
  children: ReactNode
}

export const TrendBadge = ({ tone, children }: TrendBadgeProps) => (
  <span className={`${styles.badge} ${styles[tone]}`}>
    {ARROWS[tone] && (
      <span className={styles.arrow} aria-hidden="true">
        {ARROWS[tone]}
      </span>
    )}
    {children}
  </span>
)

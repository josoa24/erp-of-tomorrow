import type { ReactNode } from 'react'
import { cn } from '@/lib/utils'

export type BadgeTone = 'up' | 'down' | 'warning' | 'neutral'

const ARROWS: Partial<Record<BadgeTone, string>> = { up: '▲', down: '▼' }

const TONES: Record<BadgeTone, { badge: string; arrow: string }> = {
  up: { badge: 'bg-nova-success-bg text-[#d9fff4]', arrow: 'text-nova-success' },
  down: { badge: 'bg-nova-danger-bg text-[#ffe1e6]', arrow: 'text-nova-danger' },
  warning: { badge: 'bg-nova-warning-bg text-[#ffeec2]', arrow: '' },
  neutral: { badge: 'bg-[rgba(140,145,255,0.18)] text-[#e1e3ff]', arrow: '' },
}

interface TrendBadgeProps {
  tone: BadgeTone
  children: ReactNode
}

export const TrendBadge = ({ tone, children }: TrendBadgeProps) => (
  <span
    className={cn(
      'inline-flex h-8 items-center gap-2 rounded-full px-3.5 text-base font-medium whitespace-nowrap',
      TONES[tone].badge,
    )}
  >
    {ARROWS[tone] && (
      <span className={cn('text-xs', TONES[tone].arrow)} aria-hidden="true">
        {ARROWS[tone]}
      </span>
    )}
    {children}
  </span>
)

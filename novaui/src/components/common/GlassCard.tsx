import type { ReactNode } from 'react'
import { cn } from '@/lib/utils'

type GlassVariant = 'default' | 'accent' | 'outline'

const VARIANTS: Record<GlassVariant, string> = {
  default: 'bg-glass shadow-glass',
  accent: 'bg-glass-accent border-[rgba(170,140,255,0.5)] shadow-glass-accent',
  outline: 'border-[rgba(108,110,255,0.6)] bg-[rgba(16,18,66,0.55)] shadow-glass-outline',
}

interface GlassCardProps {
  variant?: GlassVariant
  className?: string
  children: ReactNode
}

export const GlassCard = ({ variant = 'default', className, children }: GlassCardProps) => (
  <section
    className={cn(
      'relative rounded-card border border-nova-line backdrop-blur-[14px]',
      VARIANTS[variant],
      className,
    )}
  >
    {children}
  </section>
)

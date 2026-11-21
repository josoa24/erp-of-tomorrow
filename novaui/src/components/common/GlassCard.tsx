import type { ReactNode } from 'react'
import styles from './GlassCard.module.css'

interface GlassCardProps {
  variant?: 'default' | 'accent' | 'outline'
  className?: string
  children: ReactNode
}

export const GlassCard = ({ variant = 'default', className = '', children }: GlassCardProps) => (
  <section className={`${styles.card} ${styles[variant]} ${className}`}>{children}</section>
)

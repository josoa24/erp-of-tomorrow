import type { ButtonHTMLAttributes } from 'react'
import { cn } from '@/lib/utils'

type PillVariant = 'light' | 'ghost'

const VARIANTS: Record<PillVariant, string> = {
  light:
    'bg-white text-[#0b0c2e] shadow-[0_6px_24px_rgba(140,120,255,0.25)] hover:not-disabled:shadow-[0_8px_30px_rgba(150,130,255,0.45)]',
  ghost: 'border border-nova-line bg-transparent text-nova-text',
}

interface PillButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: PillVariant
}

export const PillButton = ({ variant = 'light', className, ...props }: PillButtonProps) => (
  <button
    className={cn(
      'h-11 w-full rounded-full text-lg font-semibold transition duration-200 hover:not-disabled:-translate-y-px disabled:opacity-65',
      VARIANTS[variant],
      className,
    )}
    {...props}
  />
)

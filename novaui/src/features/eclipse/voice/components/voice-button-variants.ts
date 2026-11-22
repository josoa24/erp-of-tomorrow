import { cva } from 'class-variance-authority'

export const voiceButtonVariants = cva(
  'relative inline-flex size-11 shrink-0 items-center justify-center rounded-pill outline-none transition-colors focus-visible:shadow-focus-ring disabled:opacity-40 [&_svg]:size-5',
  {
    variants: {
      variant: {
        night: 'text-ink-inverse enabled:hover:bg-ink-inverse/16',
        light: 'text-ink-primary enabled:hover:bg-state-hover',
      },
      active: { true: 'bg-status-critical-fg text-ink-inverse enabled:hover:bg-status-critical-fg', false: '' },
    },
    defaultVariants: { variant: 'light', active: false },
  },
)

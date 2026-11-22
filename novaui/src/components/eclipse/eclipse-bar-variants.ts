import { cva } from 'class-variance-authority'

export const eclipseBarVariants = cva(
  'relative flex h-16 w-full items-center gap-3 overflow-hidden rounded-pill px-2.5 transition-shadow',
  {
    variants: {
      variant: { night: 'text-ink-inverse', light: 'border text-ink-primary' },
      unavailable: { true: 'bg-surface-sunken', false: 'focus-within:shadow-focus-ring' },
    },
    compoundVariants: [
      { variant: 'night', unavailable: false, className: 'bg-night-bar' },
      { variant: 'light', unavailable: false, className: 'border-line-strong bg-surface-solid shadow-elevation-2' },
      { variant: 'light', unavailable: true, className: 'border-line-subtle text-ink-tertiary' },
      { variant: 'night', unavailable: true, className: 'text-ink-tertiary' },
    ],
    defaultVariants: { variant: 'light', unavailable: false },
  },
)

export const eclipseOrbVariants = cva(
  'relative inline-flex size-11 shrink-0 items-center justify-center rounded-pill text-ink-inverse [&_svg]:size-5',
  { variants: { variant: { night: 'bg-ink-inverse/16', light: 'bg-night-orb' } }, defaultVariants: { variant: 'light' } },
)

export const eclipseSendVariants = cva(
  'relative inline-flex size-11 shrink-0 items-center justify-center rounded-pill outline-none transition-opacity focus-visible:shadow-focus-ring disabled:opacity-40 [&_svg]:size-5',
  {
    variants: {
      variant: { night: 'bg-surface-solid text-ink-primary', light: 'bg-surface-night text-ink-inverse' },
    },
    defaultVariants: { variant: 'light' },
  },
)

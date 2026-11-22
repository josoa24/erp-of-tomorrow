import { cva } from 'class-variance-authority';

export const iconButtonVariants = cva(
  [
    'inline-flex shrink-0 items-center justify-center rounded-pill outline-none transition-colors',
    'focus-visible:shadow-focus-ring disabled:opacity-40 aria-disabled:opacity-40',
    '[&_svg]:shrink-0',
  ],
  {
    variants: {
      variant: {
        ghost: 'bg-transparent text-ink-secondary enabled:hover:bg-state-hover enabled:active:bg-state-pressed',
        surface: [
          'bg-surface-solid text-ink-primary',
          'enabled:hover:bg-state-hover enabled:active:bg-state-pressed',
        ],
        night: 'bg-surface-night text-ink-inverse enabled:hover:bg-action-primary-hover',
      },
      size: {
        l: 'size-11 [&_svg]:size-5',
        m: 'size-9 [&_svg]:size-[18px]',
        s: 'size-8 [&_svg]:size-4',
      },
    },
    defaultVariants: { variant: 'ghost', size: 'l' },
  },
);

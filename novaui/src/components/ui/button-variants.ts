import { cva } from 'class-variance-authority';

export const buttonVariants = cva(
  [
    'relative inline-flex shrink-0 items-center justify-center rounded-pill whitespace-nowrap',
    'outline-none transition-[background-color,box-shadow,opacity] focus-visible:shadow-focus-ring',
    'disabled:opacity-40 aria-disabled:opacity-40 aria-busy:cursor-progress',
    '[&_svg]:shrink-0',
  ],
  {
    variants: {
      variant: {
        primary: [
          'bg-surface-night text-ink-inverse',
          'enabled:hover:bg-action-primary-hover enabled:active:bg-brand-indigo',
        ],
        secondary: [
          'border border-line-strong bg-surface-solid text-ink-primary',
          'enabled:hover:bg-state-hover enabled:active:bg-state-pressed',
        ],
        ghost: [
          'bg-transparent text-ink-primary',
          'enabled:hover:bg-state-hover enabled:active:bg-state-pressed',
        ],
        eclipse: [
          'bg-night text-ink-inverse',
          'enabled:hover:shadow-eclipse-hover enabled:active:opacity-90',
        ],
        danger: [
          'bg-action-danger text-ink-inverse',
          'enabled:hover:bg-action-danger-hover enabled:active:bg-action-danger-pressed',
        ],
      },
      size: {
        m: 'h-12 gap-2 px-6 text-body-strong [&_svg]:size-5',
        s: 'h-9 gap-1.5 px-4 text-label [&_svg]:size-4',
      },
    },
    defaultVariants: { variant: 'primary', size: 'm' },
  },
);

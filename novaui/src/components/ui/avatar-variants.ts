import { cva } from 'class-variance-authority';

export const avatarVariants = cva(
  'inline-flex shrink-0 items-center justify-center rounded-pill whitespace-nowrap select-none',
  {
    variants: {
      size: {
        s: 'size-7 text-micro',
        m: 'size-9 text-label',
        l: 'size-14 text-heading',
      },
      tone: {
        lilac: 'bg-status-info-vivid text-brand-indigo',
        mint: 'bg-status-success-vivid text-status-success-fg',
        blush: 'bg-bg-blush text-ink-primary',
      },
    },
    defaultVariants: { size: 'm', tone: 'lilac' },
  },
);

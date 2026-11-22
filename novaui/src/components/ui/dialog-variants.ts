import { cva } from 'class-variance-authority';

export const dialogVariants = cva(
  'relative flex max-h-[calc(100dvh-32px)] w-full flex-col overflow-hidden bg-surface-solid shadow-elevation-2 outline-none',
  {
    variants: {
      placement: {
        center: 'rounded-xl',
        sheet: 'mt-auto rounded-t-xl',
      },
      size: { m: 'max-w-[640px]', l: 'max-w-[880px]' },
    },
    compoundVariants: [{ placement: 'sheet', className: 'max-w-none' }],
    defaultVariants: { placement: 'center', size: 'm' },
  },
);

export const dialogGutter = {
  center: 'px-6 sm:px-8',
  sheet: 'px-4',
} as const;

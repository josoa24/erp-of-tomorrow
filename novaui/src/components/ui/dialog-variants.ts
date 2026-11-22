import { cva } from 'class-variance-authority';

export const dialogVariants = cva(
  'relative flex max-h-[calc(100dvh-32px)] w-full flex-col gap-6 overflow-y-auto bg-surface-solid shadow-elevation-2 outline-none',
  {
    variants: {
      placement: {
        center: 'rounded-xl p-6 sm:p-8',
        sheet: 'mt-auto rounded-t-xl px-4 pt-6 pb-8',
      },
      size: { m: 'max-w-[640px]', l: 'max-w-[880px]' },
    },
    compoundVariants: [{ placement: 'sheet', className: 'max-w-none' }],
    defaultVariants: { placement: 'center', size: 'm' },
  },
);

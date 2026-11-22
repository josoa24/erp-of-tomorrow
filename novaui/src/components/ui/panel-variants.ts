import { cva } from 'class-variance-authority';

export const panelVariants = cva('min-w-0', {
  variants: {
    tone: {
      panel: 'bg-surface-panel',
      solid: 'bg-surface-solid',
      sunken: 'bg-surface-sunken',
      elevated: 'bg-surface-solid shadow-elevation-1',
    },
    radius: { lg: 'rounded-lg', xl: 'rounded-xl' },
    padding: { none: 'p-0', s: 'p-3', m: 'p-5', l: 'p-6', xl: 'p-8' },
  },
  defaultVariants: { tone: 'panel', radius: 'xl', padding: 'm' },
});

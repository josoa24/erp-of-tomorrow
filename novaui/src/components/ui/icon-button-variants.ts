import { cva } from 'class-variance-authority';

/**
 * Bouton icône (Figma « Icon Button »). Actions compactes : notifications, fermer,
 * plus d’options… Ghost neutre par défaut, icône renforcée au survol.
 * À accompagner d’une infobulle (`<Tooltip>`).
 */
export const iconButtonVariants = cva(
  [
    'inline-flex shrink-0 items-center justify-center rounded-md outline-none transition-colors',
    'focus-visible:shadow-focus-ring disabled:pointer-events-none',
    '[&_svg]:size-4 [&_svg]:shrink-0 disabled:[&_svg]:text-text-disabled',
  ],
  {
    variants: {
      variant: {
        ghost: [
          '[&_svg]:text-icon-muted hover:bg-bg-hover hover:[&_svg]:text-icon-default',
          'focus-visible:bg-bg-hover active:bg-bg-pressed active:[&_svg]:text-icon-default',
        ],
        secondary: [
          'border border-border-strong bg-bg-surface [&_svg]:text-icon-default',
          'hover:bg-bg-hover active:bg-bg-pressed disabled:border-border-default',
        ],
      },
      size: {
        m: 'size-(--layout-control-height)',
        s: 'size-8',
      },
    },
    defaultVariants: { variant: 'ghost', size: 'm' },
  },
);

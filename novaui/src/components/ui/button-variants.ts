import { cva } from 'class-variance-authority';

/**
 * Bouton d’action (Figma « Button »).
 * - primary : une seule action principale par zone
 * - secondary : actions courantes
 * - ghost : actions tertiaires, neutre
 * - danger : action destructive confirmée
 * Tailles : m = layout/control-height (40 Desktop, 44 Mobile), s = 32 (barres d’outils, tableaux).
 */
export const buttonVariants = cva(
  [
    'inline-flex shrink-0 items-center justify-center whitespace-nowrap rounded-md font-medium',
    'outline-none transition-colors focus-visible:shadow-focus-ring',
    'disabled:pointer-events-none disabled:bg-bg-disabled disabled:text-text-disabled disabled:[&_svg]:text-text-disabled',
    'aria-busy:pointer-events-none',
    '[&_svg]:size-4 [&_svg]:shrink-0',
  ],
  {
    variants: {
      variant: {
        primary: [
          'bg-bg-primary text-text-on-primary [&_svg]:text-icon-on-primary',
          'hover:bg-bg-primary-hover active:bg-bg-primary-pressed',
        ],
        secondary: [
          'border border-border-strong bg-bg-surface text-text-primary [&_svg]:text-icon-default',
          'hover:bg-bg-hover active:bg-bg-pressed disabled:border-border-default',
        ],
        ghost: [
          'text-text-secondary [&_svg]:text-icon-muted',
          'hover:bg-bg-hover hover:text-text-primary hover:[&_svg]:text-icon-default',
          'focus-visible:bg-bg-hover active:bg-bg-pressed active:text-text-primary',
          'disabled:bg-transparent',
        ],
        danger: [
          'bg-bg-danger text-text-on-primary [&_svg]:text-icon-on-primary',
          'hover:bg-bg-danger-hover active:bg-bg-danger-pressed',
        ],
      },
      size: {
        m: 'h-(--layout-control-height) gap-2 px-4 text-body-m',
        s: 'h-8 gap-1 px-3 text-button-s',
      },
    },
    defaultVariants: { variant: 'primary', size: 'm' },
  },
);

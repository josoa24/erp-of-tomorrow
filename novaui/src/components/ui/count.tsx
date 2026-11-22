import { cva } from 'class-variance-authority';
import type { VariantProps } from 'class-variance-authority';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

const countVariants = cva(
  'inline-flex min-w-5 items-center justify-center rounded-full font-mono text-mono-xs font-medium whitespace-nowrap',
  {
    variants: {
      tone: {
        /** Pastille, usage ponctuel. */
        neutral: 'bg-bg-primary-subtle px-2 py-0.5 text-text-link',
        /** Éléments à traiter. */
        attention: 'bg-bg-accent px-2 py-0.5 text-text-on-accent',
        muted: 'bg-bg-muted px-2 py-0.5 text-text-secondary',
        /** Chiffre seul, pour la navigation et les listes. */
        plain: 'text-text-tertiary',
      },
    },
    defaultVariants: { tone: 'neutral' },
  },
);

export type CountProps = Omit<ComponentProps<'span'>, 'children'> &
  VariantProps<typeof countVariants> & {
    value: number;
    /** Au-delà, affiche « max+ » (99+ par défaut). */
    max?: number;
  };

/** Compteur d’éléments (Figma « Count »). */
export function Count({ className, tone, value, max = 99, ...props }: CountProps) {
  return (
    <span className={cn(countVariants({ tone }), className)} {...props}>
      {value > max ? `${max}+` : value}
    </span>
  );
}

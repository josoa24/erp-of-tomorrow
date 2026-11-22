import { cva } from 'class-variance-authority';
import type { VariantProps } from 'class-variance-authority';
import { Sparkle } from 'lucide-react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

const badgeVariants = cva(
  'inline-flex items-center gap-1 rounded-sm px-2 py-0.5 text-caption font-medium whitespace-nowrap',
  {
    variants: {
      tone: {
        neutral: 'bg-bg-muted text-text-secondary',
        primary: 'bg-bg-primary-subtle text-text-link',
        success: 'bg-bg-success-subtle text-text-success',
        warning: 'bg-bg-warning-subtle text-text-warning',
        danger: 'bg-bg-danger-subtle text-text-danger',
        ai: 'bg-bg-ai-subtle text-text-ai',
      },
    },
    defaultVariants: { tone: 'neutral' },
  },
);

const indicatorColor = {
  neutral: 'bg-icon-muted',
  primary: 'bg-icon-primary',
  success: 'bg-icon-success',
  warning: 'bg-icon-warning',
  danger: 'bg-icon-danger',
} as const;

export type BadgeProps = ComponentProps<'span'> &
  VariantProps<typeof badgeVariants> & {
    /** Affiche la pastille (ou l’étoile pour le ton AI). */
    indicator?: boolean;
  };

/**
 * Emphase isolée sur un objet (Figma « Badge »). Le libellé porte le sens, la couleur
 * ne fait que le renforcer. Dans les tableaux et listes, utiliser `<Status>`.
 */
export function Badge({ className, tone, indicator = true, children, ...props }: BadgeProps) {
  const resolvedTone = tone ?? 'neutral';

  return (
    <span className={cn(badgeVariants({ tone }), className)} {...props}>
      {indicator &&
        (resolvedTone === 'ai' ? (
          <Sparkle aria-hidden="true" className="size-3 shrink-0 fill-current text-icon-ai" />
        ) : (
          <span
            aria-hidden="true"
            className={cn('size-1.5 shrink-0 rounded-full', indicatorColor[resolvedTone])}
          />
        ))}
      {children}
    </span>
  );
}

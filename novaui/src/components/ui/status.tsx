import { Sparkle } from 'lucide-react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type StatusTone = 'neutral' | 'info' | 'success' | 'warning' | 'danger' | 'ai';

const dotColor: Record<Exclude<StatusTone, 'ai'>, string> = {
  neutral: 'bg-icon-muted',
  info: 'bg-icon-info',
  success: 'bg-icon-success',
  warning: 'bg-icon-warning',
  danger: 'bg-icon-danger',
};

export type StatusProps = ComponentProps<'span'> & { tone?: StatusTone };

/**
 * Statut dans les données denses (Figma « Status ») : point de 8 px + libellé obligatoire.
 * La couleur ne porte jamais seule l’information (WCAG 1.4.1).
 */
export function Status({ className, tone = 'neutral', children, ...props }: StatusProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-2 text-body-s whitespace-nowrap text-text-primary',
        className,
      )}
      {...props}
    >
      {tone === 'ai' ? (
        <Sparkle aria-hidden="true" className="size-3 shrink-0 fill-current text-icon-ai" />
      ) : (
        <span aria-hidden="true" className={cn('size-2 shrink-0 rounded-full', dotColor[tone])} />
      )}
      {children}
    </span>
  );
}

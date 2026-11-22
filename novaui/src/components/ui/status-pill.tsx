import { Check, Clock, Info, OctagonAlert, TriangleAlert } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import { statusPillVariants } from './status-pill-variants';

export type StatusTone = 'success' | 'warning' | 'critical' | 'info' | 'neutral';
export type StatusEmphasis = 'soft' | 'vivid';

const DEFAULT_ICONS: Record<StatusTone, LucideIcon> = {
  success: Check,
  warning: TriangleAlert,
  critical: OctagonAlert,
  info: Info,
  neutral: Clock,
};

export type StatusPillProps = Omit<ComponentProps<'span'>, 'children'> & {
  label: string;
  tone?: StatusTone;
  emphasis?: StatusEmphasis;
  icon?: LucideIcon;
};

export function StatusPill({
  className,
  label,
  tone = 'neutral',
  emphasis = 'soft',
  icon,
  ...props
}: StatusPillProps) {
  const Icon = icon ?? DEFAULT_ICONS[tone];

  return (
    <span className={cn(statusPillVariants({ tone, emphasis }), className)} {...props}>
      <Icon aria-hidden="true" />
      {label}
    </span>
  );
}

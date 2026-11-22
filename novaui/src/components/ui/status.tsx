import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import type { StatusTone } from './status-pill';

const DOT_COLOR: Record<StatusTone, string> = {
  neutral: 'bg-ink-tertiary',
  info: 'bg-status-info-fg',
  success: 'bg-status-success-fg',
  warning: 'bg-status-warning-fg',
  critical: 'bg-status-critical-fg',
};

export type StatusProps = ComponentProps<'span'> & { tone?: StatusTone };

export function Status({ className, tone = 'neutral', children, ...props }: StatusProps) {
  return (
    <span
      className={cn('inline-flex items-center gap-2 text-body-s whitespace-nowrap text-ink-primary', className)}
      {...props}
    >
      <span aria-hidden="true" className={cn('size-2 shrink-0 rounded-pill', DOT_COLOR[tone])} />
      {children}
    </span>
  );
}

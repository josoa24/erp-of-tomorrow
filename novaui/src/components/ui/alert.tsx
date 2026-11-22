import { CircleCheck, Info, TriangleAlert, X } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';
import type { ComponentProps, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { IconButton } from './icon-button';

export type AlertTone = 'info' | 'success' | 'warning';

const TONES: Record<AlertTone, { icon: LucideIcon; surface: string }> = {
  info: { icon: Info, surface: 'bg-status-info-bg text-status-info-fg' },
  success: { icon: CircleCheck, surface: 'bg-status-success-bg text-status-success-fg' },
  warning: { icon: TriangleAlert, surface: 'bg-status-warning-bg text-status-warning-fg' },
};

export type AlertProps = Omit<ComponentProps<'div'>, 'title'> & {
  tone?: AlertTone;
  title: ReactNode;
  description?: ReactNode;
  action?: ReactNode;
  onDismiss?: () => void;
};

export function Alert({ className, tone = 'info', title, description, action, onDismiss, ...props }: AlertProps) {
  const { icon: Icon, surface } = TONES[tone];

  return (
    <div
      role="status"
      className={cn('flex w-full items-start gap-3 rounded-md px-4 py-3', surface, className)}
      {...props}
    >
      <Icon aria-hidden="true" className="mt-0.5 size-5 shrink-0" />
      <div className="flex min-w-0 flex-1 flex-col gap-0.5">
        <p className="text-body-strong">{title}</p>
        {description && <p className="text-body-s text-ink-secondary">{description}</p>}
      </div>
      {(action || onDismiss) && (
        <div className="flex shrink-0 items-center gap-1">
          {action}
          {onDismiss && (
            <IconButton size="s" aria-label="Fermer" onClick={onDismiss}>
              <X />
            </IconButton>
          )}
        </div>
      )}
    </div>
  );
}

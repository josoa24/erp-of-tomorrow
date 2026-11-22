import type { LucideIcon } from 'lucide-react';
import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';

export type EmptyStateProps = {
  icon: LucideIcon;
  title: string;
  description?: ReactNode;
  actions?: ReactNode;
  className?: string;
};

export function EmptyState({ icon: Icon, title, description, actions, className }: EmptyStateProps) {
  return (
    <div className={cn('flex flex-col items-center gap-3 px-4 py-12 text-center', className)}>
      <span className="inline-flex size-14 items-center justify-center rounded-pill bg-bg-lilac">
        <Icon aria-hidden="true" className="size-6 text-ink-primary" />
      </span>
      <h2 className="text-heading text-ink-primary">{title}</h2>
      {description && <p className="max-w-[48ch] text-body text-ink-secondary">{description}</p>}
      {actions && <div className="mt-2 flex flex-wrap items-center justify-center gap-3">{actions}</div>}
    </div>
  );
}

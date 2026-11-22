import type { LucideIcon } from 'lucide-react';
import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';

export type StatTone = 'lilac' | 'mint' | 'blush';

export type StatProps = {
  icon: LucideIcon;
  label: string;
  value: ReactNode;
  hint?: ReactNode;
  tone?: StatTone;
  highlighted?: boolean;
  className?: string;
};

const PASTILLE: Record<StatTone, string> = {
  lilac: 'bg-bg-lilac',
  mint: 'bg-bg-mint',
  blush: 'bg-bg-blush',
};

export function Stat({ icon: Icon, label, value, hint, tone = 'lilac', highlighted = false, className }: StatProps) {
  return (
    <div
      className={cn(
        'flex min-w-0 flex-1 flex-col gap-1.5',
        highlighted && 'rounded-lg px-3 py-2 highlight-added',
        className,
      )}
    >
      <div className="flex items-center gap-2">
        <span className={cn('inline-flex size-7 shrink-0 items-center justify-center rounded-pill', PASTILLE[tone])}>
          <Icon aria-hidden="true" className="size-3.5 text-ink-primary" />
        </span>
        <span className="truncate text-caption text-ink-secondary uppercase">{label}</span>
      </div>
      <span className="text-number-m text-ink-primary">{value}</span>
      {hint && <span className="text-micro text-ink-tertiary">{hint}</span>}
    </div>
  );
}

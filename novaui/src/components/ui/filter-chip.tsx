import { X } from 'lucide-react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type FilterChipProps = Omit<ComponentProps<'button'>, 'children'> & {
  label: string;
  count?: number;
  active?: boolean;
  highlighted?: boolean;
};

export function FilterChip({
  className,
  label,
  count,
  active = false,
  highlighted = false,
  type = 'button',
  ...props
}: FilterChipProps) {
  const chip = (
    <button
      type={type}
      aria-pressed={active}
      className={cn(
        'inline-flex h-9 shrink-0 items-center gap-2 rounded-pill border px-3.5 text-label whitespace-nowrap',
        'outline-none transition-colors focus-visible:shadow-focus-ring disabled:opacity-40',
        active
          ? 'border-brand-lilac bg-state-selected pr-2.5 text-status-info-fg'
          : 'border-line-strong bg-surface-solid text-ink-primary enabled:hover:bg-state-hover',
        className,
      )}
      {...props}
    >
      {label}
      {count !== undefined && (
        <span
          className={cn(
            'inline-flex h-5 min-w-5 items-center justify-center rounded-pill px-1.5 text-micro text-ink-secondary',
            active ? 'bg-surface-solid' : 'bg-surface-sunken',
          )}
        >
          {count}
        </span>
      )}
      {active && <X aria-hidden="true" className="size-3.5 shrink-0" />}
    </button>
  );

  if (!highlighted) return chip;
  return <span className="inline-flex shrink-0 rounded-pill p-[3px] highlight-added">{chip}</span>;
}

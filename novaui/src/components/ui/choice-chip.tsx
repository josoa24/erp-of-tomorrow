import type { LucideIcon } from 'lucide-react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type ChoiceChipProps = Omit<ComponentProps<'button'>, 'children'> & {
  label: string;
  icon?: LucideIcon;
  selected?: boolean;
};

export function ChoiceChip({ className, label, icon: Icon, selected = false, type = 'button', ...props }: ChoiceChipProps) {
  return (
    <button
      type={type}
      aria-pressed={selected}
      className={cn(
        'inline-flex h-11 shrink-0 items-center gap-2 rounded-pill border pr-4 pl-3.5 text-label whitespace-nowrap',
        'outline-none transition-colors focus-visible:shadow-focus-ring disabled:opacity-40',
        selected
          ? 'border-surface-night bg-surface-night text-ink-inverse'
          : 'border-line-strong bg-surface-solid text-ink-primary enabled:hover:bg-state-hover',
        className,
      )}
      {...props}
    >
      {Icon && <Icon aria-hidden="true" className="size-[18px] shrink-0" />}
      {label}
    </button>
  );
}

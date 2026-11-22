import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { Count } from './count';
import { useTabsKeyboard } from './use-tabs-keyboard';

export type TabItem<T extends string> = {
  value: T;
  label: string;
  icon?: ReactNode;
  count?: number;
  disabled?: boolean;
};

export type TabsProps<T extends string> = {
  items: TabItem<T>[];
  value: T;
  onValueChange: (value: T) => void;
  variant?: 'underline' | 'segmented';
  'aria-label': string;
  className?: string;
};

export function Tabs<T extends string>({
  items,
  value,
  onValueChange,
  variant = 'underline',
  'aria-label': ariaLabel,
  className,
}: TabsProps<T>) {
  const { listRef, onKeyDown } = useTabsKeyboard(items, value, onValueChange);
  const segmented = variant === 'segmented';

  return (
    <div
      ref={listRef}
      role="tablist"
      aria-label={ariaLabel}
      onKeyDown={onKeyDown}
      className={cn(
        'flex items-center',
        segmented
          ? 'inline-flex w-fit gap-1 rounded-pill bg-surface-panel p-1'
          : 'gap-6 border-b border-line-subtle',
        className,
      )}
    >
      {items.map((item) => {
        const selected = item.value === value;
        return (
          <button
            key={item.value}
            type="button"
            role="tab"
            data-value={item.value}
            aria-selected={selected}
            tabIndex={selected ? 0 : -1}
            disabled={item.disabled}
            onClick={() => onValueChange(item.value)}
            className={cn(
              'inline-flex shrink-0 items-center justify-center gap-2 text-label whitespace-nowrap',
              'text-ink-secondary outline-none transition-colors focus-visible:shadow-focus-ring',
              'hover:text-ink-primary disabled:opacity-40 [&_svg]:size-4 [&_svg]:shrink-0',
              segmented
                ? cn('h-9 rounded-pill px-4', selected && 'bg-surface-night text-ink-inverse hover:text-ink-inverse')
                : cn(
                    '-mb-px h-11 border-b-2 border-transparent px-1 focus-visible:rounded-xs',
                    selected && 'border-ink-primary text-ink-primary',
                  ),
            )}
          >
            {item.icon}
            {item.label}
            {item.count !== undefined && <Count value={item.count} />}
          </button>
        );
      })}
    </div>
  );
}

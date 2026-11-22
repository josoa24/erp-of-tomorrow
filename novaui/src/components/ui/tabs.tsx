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
  /** underline : navigation de page ; segmented : bascule de vue compacte. */
  variant?: 'underline' | 'segmented';
  'aria-label': string;
  className?: string;
};

/**
 * Groupe d’onglets (Figma « Tabs » / « Tab »). Un seul onglet sélectionné par groupe.
 * Navigation clavier : flèches gauche/droite, Début, Fin (onglets désactivés ignorés).
 */
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
          ? 'inline-flex w-fit gap-1 rounded-lg bg-bg-muted p-1'
          : 'gap-6 border-b border-border-default',
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
              'inline-flex shrink-0 items-center justify-center gap-2 text-body-s font-medium whitespace-nowrap',
              'text-text-secondary outline-none transition-colors focus-visible:shadow-focus-ring',
              'hover:text-text-primary focus-visible:bg-bg-hover disabled:pointer-events-none disabled:text-text-disabled',
              '[&_svg]:size-4 [&_svg]:shrink-0',
              segmented
                ? cn(
                    'h-8 rounded-md px-3',
                    selected && 'bg-bg-surface text-text-primary shadow-card',
                  )
                : cn(
                    // -mb-px : la soulignure recouvre la bordure du groupe.
                    '-mb-px h-10 border-b-2 border-transparent px-1 focus-visible:rounded-md',
                    'hover:border-border-strong',
                    selected && 'border-border-primary text-text-link hover:border-border-primary',
                  ),
            )}
          >
            {item.icon}
            {item.label}
            {item.count !== undefined && <Count tone="muted" value={item.count} />}
          </button>
        );
      })}
    </div>
  );
}

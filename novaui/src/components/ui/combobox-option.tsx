import { Check } from 'lucide-react';
import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';
import type { ComboboxOption } from './combobox-types';

export type ComboboxOptionRowProps = {
  id: string;
  option: ComboboxOption | null;
  emptyLabel: string;
  active: boolean;
  selected: boolean;
  render?: (option: ComboboxOption) => ReactNode;
  onPick: () => void;
  onHover: () => void;
};

export function ComboboxOptionRow({ id, option, emptyLabel, active, selected, render, onPick, onHover }: ComboboxOptionRowProps) {
  return (
    <div
      id={id}
      role="option"
      aria-selected={selected}
      onClick={onPick}
      onMouseMove={onHover}
      className={cn(
        'flex min-h-11 cursor-pointer items-center gap-3 rounded-sm px-3 py-2 text-body text-ink-primary',
        selected && 'bg-state-selected font-medium',
        active && (selected ? 'bg-state-pressed' : 'bg-state-hover'),
      )}
    >
      <span className={cn('min-w-0 flex-1', !option && 'text-ink-secondary')}>
        {!option ? emptyLabel : render ? render(option) : (
          <>
            <span className="block truncate">{option.label}</span>
            {option.description && <span className="block truncate text-micro text-ink-tertiary">{option.description}</span>}
          </>
        )}
      </span>
      {selected && <Check aria-hidden="true" className="size-4 shrink-0 text-brand-violet" />}
    </div>
  );
}

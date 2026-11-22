import { Check } from 'lucide-react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type ChipProps = Omit<ComponentProps<'button'>, 'children'> & {
  label: string;
  count?: number;
  selected?: boolean;
};

/**
 * Filtre ou catégorie sélectionnable (Figma « Chip »). L’état sélectionné ajoute une
 * coche : il ne repose pas que sur la couleur.
 */
export function Chip({
  className,
  label,
  count,
  selected = false,
  type = 'button',
  ...props
}: ChipProps) {
  return (
    <button
      type={type}
      aria-pressed={selected}
      className={cn(
        'inline-flex h-9 shrink-0 items-center gap-1 rounded-full border border-border-strong bg-bg-surface px-3',
        'text-body-s font-medium whitespace-nowrap text-text-primary outline-none transition-colors',
        'hover:bg-bg-hover focus-visible:shadow-focus-ring',
        'disabled:pointer-events-none disabled:border-border-default disabled:text-text-disabled',
        selected && 'border-border-primary bg-bg-selected text-text-link hover:bg-bg-selected',
        className,
      )}
      {...props}
    >
      {selected && <Check aria-hidden="true" className="size-4 shrink-0 text-icon-primary" />}
      {label}
      {count !== undefined && (
        <span className="font-mono text-mono-xs font-medium text-text-tertiary in-disabled:text-text-disabled">
          {count}
        </span>
      )}
    </button>
  );
}

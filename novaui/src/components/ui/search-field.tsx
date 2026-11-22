import { Search } from 'lucide-react';
import { useEffect, useRef } from 'react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import { Kbd } from './kbd';

export type SearchFieldProps = Omit<ComponentProps<'input'>, 'type' | 'size'> & {
  /** Active le raccourci global Ctrl K / ⌘K et l’affiche dans le champ. */
  shortcut?: boolean;
  containerClassName?: string;
};

const isMac = typeof navigator !== 'undefined' && /Mac|iPhone|iPad/.test(navigator.platform);

/** Recherche globale (Figma « Search Field »). */
export function SearchField({
  shortcut = false,
  placeholder = 'Rechercher…',
  'aria-label': ariaLabel = 'Rechercher',
  className,
  containerClassName,
  ref,
  ...props
}: SearchFieldProps) {
  const inputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    if (!shortcut) return;
    const onKeyDown = (event: KeyboardEvent) => {
      if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') {
        event.preventDefault();
        inputRef.current?.focus();
      }
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [shortcut]);

  return (
    <div
      className={cn(
        'flex h-(--layout-control-height) w-full items-center gap-2 rounded-md border border-border-control bg-bg-surface pr-2 pl-3',
        'transition-colors focus-within:border-[1.5px] focus-within:border-border-primary focus-within:shadow-focus-ring',
        containerClassName,
      )}
    >
      <Search aria-hidden="true" className="size-5 shrink-0 text-icon-muted" />
      <input
        ref={(node) => {
          inputRef.current = node;
          if (typeof ref === 'function') ref(node);
          else if (ref) ref.current = node;
        }}
        type="search"
        placeholder={placeholder}
        aria-label={ariaLabel}
        className={cn(
          'min-w-0 flex-1 bg-transparent text-body-m text-text-primary outline-none placeholder:text-text-tertiary',
          '[&::-webkit-search-cancel-button]:hidden',
          className,
        )}
        {...props}
      />
      {shortcut && <Kbd aria-hidden="true">{isMac ? '⌘ K' : 'Ctrl K'}</Kbd>}
    </div>
  );
}

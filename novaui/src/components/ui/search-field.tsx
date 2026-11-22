import { Search } from 'lucide-react';
import { useRef, useState } from 'react';
import type { ChangeEvent, ComponentProps } from 'react';
import { useDebouncedCallback } from '@/hooks/useDebouncedCallback';
import { useFocusShortcut } from '@/hooks/useFocusShortcut';
import { cn } from '@/lib/utils';

export const SEARCH_DEBOUNCE_MS = 250;

export type SearchFieldProps = Omit<ComponentProps<'input'>, 'type' | 'size' | 'value' | 'defaultValue' | 'onChange'> & {
  defaultValue?: string;
  onSearch: (value: string) => void;
  delay?: number;
  slashShortcut?: boolean;
  containerClassName?: string;
};

export function SearchField({
  defaultValue = '',
  onSearch,
  delay = SEARCH_DEBOUNCE_MS,
  slashShortcut = true,
  placeholder = 'Rechercher…',
  'aria-label': ariaLabel,
  className,
  containerClassName,
  ...props
}: SearchFieldProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [value, setValue] = useState(defaultValue);
  const debouncedSearch = useDebouncedCallback(onSearch, delay);
  useFocusShortcut(inputRef, 'slash', slashShortcut);

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    setValue(event.target.value);
    debouncedSearch(event.target.value.trim());
  };

  return (
    <div
      className={cn(
        'flex h-11 w-full items-center gap-2.5 rounded-pill border border-line-strong bg-surface-solid pr-3 pl-4',
        'transition-shadow focus-within:border-focus-ring focus-within:shadow-focus-ring',
        containerClassName,
      )}
    >
      <Search aria-hidden="true" className="size-[18px] shrink-0 text-ink-secondary" />
      <input
        ref={inputRef}
        type="search"
        value={value}
        onChange={handleChange}
        placeholder={placeholder}
        aria-label={ariaLabel ?? placeholder}
        className={cn(
          'min-w-0 flex-1 bg-transparent text-body text-ink-primary outline-none placeholder:text-ink-tertiary',
          '[&::-webkit-search-cancel-button]:hidden',
          className,
        )}
        {...props}
      />
    </div>
  );
}

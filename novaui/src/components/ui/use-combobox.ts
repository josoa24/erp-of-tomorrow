import { useCallback, useEffect, useMemo, useState } from 'react';
import type { ChangeEvent, KeyboardEvent } from 'react';
import { useDebouncedCallback } from '@/hooks/useDebouncedCallback';
import { SEARCH_DEBOUNCE_MS } from './search-field';
import type { ComboboxItem, ComboboxOption, ComboboxProps } from './combobox-types';

const matches = (option: ComboboxOption, query: string) =>
  `${option.label} ${option.description ?? ''}`.toLowerCase().includes(query.toLowerCase());

type Args = Pick<
  ComboboxProps,
  'options' | 'value' | 'onChange' | 'onSelect' | 'onQueryChange' | 'emptyOptionLabel'
> & { id: string };

export const useCombobox = ({ id, options, value = null, onChange, onSelect, onQueryChange, emptyOptionLabel }: Args) => {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');
  const [typing, setTyping] = useState(false);
  const [active, setActive] = useState(0);
  const debounced = useDebouncedCallback((text: string) => onQueryChange?.(text), SEARCH_DEBOUNCE_MS);
  const actionMode = onSelect !== undefined;

  const items = useMemo<ComboboxItem[]>(() => {
    const visible = onQueryChange || !typing ? options : options.filter((option) => matches(option, query.trim()));
    const list = visible.map((option) => ({ key: option.value, option }));
    return emptyOptionLabel && !typing ? [{ key: '', option: null }, ...list] : list;
  }, [options, onQueryChange, typing, query, emptyOptionLabel]);

  const reset = useCallback(() => {
    setOpen(false);
    setQuery('');
    setTyping(false);
    if (typing) debounced('');
  }, [typing, debounced]);

  const openAt = useCallback(() => {
    const selected = items.findIndex((item) => item.option?.value === value?.value);
    setActive(Math.max(selected, 0));
    setOpen(true);
  }, [items, value]);

  const pick = (item: ComboboxItem) => {
    if (actionMode && item.option) onSelect(item.option);
    else onChange?.(item.option);
    reset();
  };

  const move = (next: number) => setActive(items.length ? (next + items.length) % items.length : 0);

  const onInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setQuery(event.target.value);
    setTyping(true);
    setActive(0);
    setOpen(true);
    debounced(event.target.value.trim());
  };

  const onKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    const { key } = event;
    if (key === 'Escape' && open) {
      event.preventDefault();
      event.stopPropagation();
      reset();
    } else if (key === 'Tab') reset();
    else if (key.length === 1 && !typing && !event.ctrlKey && !event.metaKey) event.currentTarget.select();
    else if (key === 'ArrowDown' || key === 'ArrowUp') {
      event.preventDefault();
      if (!open) openAt();
      else move(active + (key === 'ArrowDown' ? 1 : -1));
    } else if (open && (key === 'Home' || key === 'End')) {
      event.preventDefault();
      move(key === 'Home' ? 0 : -1);
    } else if (key === 'Enter' && open) {
      event.preventDefault();
      if (items[active]) pick(items[active]);
    }
  };

  useEffect(() => {
    if (open) document.getElementById(`${id}-opt-${active}`)?.scrollIntoView({ block: 'nearest' });
  }, [open, active, id]);

  return {
    open, query, typing, active, items, actionMode,
    setActive, openAt, reset, pick, onInputChange, onKeyDown,
  };
};

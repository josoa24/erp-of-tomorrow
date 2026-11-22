import { useRef } from 'react';
import type { KeyboardEvent } from 'react';
import type { TabItem } from './tabs';

export function useTabsKeyboard<T extends string>(
  items: TabItem<T>[],
  value: T,
  onValueChange: (value: T) => void,
) {
  const listRef = useRef<HTMLDivElement>(null);

  const onKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    const enabled = items.filter((item) => !item.disabled);
    const index = enabled.findIndex((item) => item.value === value);
    const keyToIndex: Record<string, number> = {
      ArrowRight: (index + 1) % enabled.length,
      ArrowLeft: (index - 1 + enabled.length) % enabled.length,
      Home: 0,
      End: enabled.length - 1,
    };
    const nextIndex = keyToIndex[event.key];
    const next = nextIndex === undefined ? undefined : enabled[nextIndex];
    if (!next) return;
    event.preventDefault();
    onValueChange(next.value);
    listRef.current
      ?.querySelector<HTMLButtonElement>(`[data-value="${CSS.escape(next.value)}"]`)
      ?.focus();
  };

  return { listRef, onKeyDown };
}

const ITEM_SELECTOR = '[role="menuitem"]:not([disabled])';

export const menuItemsOf = (container: HTMLElement | null): HTMLElement[] =>
  container ? Array.from(container.querySelectorAll<HTMLElement>(ITEM_SELECTOR)) : [];

export const nextMenuIndex = (key: string, current: number, count: number): number | null => {
  if (count === 0) return null;
  if (key === 'ArrowDown') return (current + 1) % count;
  if (key === 'ArrowUp') return (current - 1 + count) % count;
  if (key === 'Home') return 0;
  if (key === 'End') return count - 1;
  return null;
};

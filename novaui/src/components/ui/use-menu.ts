import { useCallback, useEffect, useId, useRef, useState } from 'react';
import type { KeyboardEvent, RefObject } from 'react';
import { menuItemsOf, nextMenuIndex } from './menu-navigation';
import type { MenuAlign } from './menu-position';
import { useMenuPosition } from './use-menu-position';

export type MenuTriggerProps = {
  ref: RefObject<HTMLButtonElement | null>;
  'aria-haspopup': 'menu';
  'aria-expanded': boolean;
  'aria-controls'?: string;
  onClick: () => void;
  onKeyDown: (event: KeyboardEvent<HTMLButtonElement>) => void;
};

export const useMenu = (align: MenuAlign = 'end') => {
  const [open, setOpen] = useState(false);
  const triggerRef = useRef<HTMLButtonElement>(null);
  const contentRef = useRef<HTMLDivElement>(null);
  const menuId = useId();

  const close = useCallback(() => {
    setOpen(false);
    triggerRef.current?.focus();
  }, []);

  const dismiss = useCallback(() => setOpen(false), []);
  const position = useMenuPosition(open, align, triggerRef, contentRef, dismiss);

  useEffect(() => {
    if (open) menuItemsOf(contentRef.current)[0]?.focus({ preventScroll: true });
  }, [open]);

  useEffect(() => {
    if (!open) return;
    const onPointerDown = (event: PointerEvent) => {
      const target = event.target as Node;
      if (contentRef.current?.contains(target) || triggerRef.current?.contains(target)) return;
      dismiss();
    };
    document.addEventListener('pointerdown', onPointerDown);
    return () => document.removeEventListener('pointerdown', onPointerDown);
  }, [open, dismiss]);

  const onContentKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Escape') {
      event.stopPropagation();
      return close();
    }
    if (event.key === 'Tab') return close();
    const items = menuItemsOf(contentRef.current);
    const next = nextMenuIndex(event.key, items.indexOf(document.activeElement as HTMLElement), items.length);
    if (next === null) return;
    event.preventDefault();
    items[next]?.focus({ preventScroll: true });
  };

  const triggerProps: MenuTriggerProps = {
    ref: triggerRef,
    'aria-haspopup': 'menu',
    'aria-expanded': open,
    'aria-controls': open ? menuId : undefined,
    onClick: () => setOpen((current) => !current),
    onKeyDown: (event) => {
      if (event.key !== 'ArrowDown') return;
      event.preventDefault();
      setOpen(true);
    },
  };

  return { open, close, triggerProps, contentRef, menuId, onContentKeyDown, position };
};

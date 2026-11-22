import { useEffect, useLayoutEffect, useState } from 'react';
import type { RefObject } from 'react';
import { computeMenuPosition } from './menu-position';
import type { MenuAlign, MenuPosition } from './menu-position';

export const useMenuPosition = (
  open: boolean,
  align: MenuAlign,
  triggerRef: RefObject<HTMLElement | null>,
  contentRef: RefObject<HTMLElement | null>,
  onDismiss: () => void,
): MenuPosition => {
  const [position, setPosition] = useState<MenuPosition>({});

  useLayoutEffect(() => {
    if (!open || !triggerRef.current || !contentRef.current) return;
    const viewport = { width: window.innerWidth, height: window.innerHeight };
    setPosition(
      computeMenuPosition(triggerRef.current.getBoundingClientRect(), contentRef.current.offsetHeight, viewport, align),
    );
  }, [open, align, triggerRef, contentRef]);

  useEffect(() => {
    if (!open) return;
    const onScroll = (event: Event) => {
      if (contentRef.current?.contains(event.target as Node)) return;
      onDismiss();
    };
    window.addEventListener('resize', onDismiss);
    window.addEventListener('scroll', onScroll, true);
    return () => {
      window.removeEventListener('resize', onDismiss);
      window.removeEventListener('scroll', onScroll, true);
    };
  }, [open, contentRef, onDismiss]);

  return position;
};

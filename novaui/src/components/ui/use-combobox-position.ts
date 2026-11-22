import { useCallback, useEffect, useLayoutEffect, useState } from 'react';
import type { RefObject } from 'react';
import { computeComboboxPosition } from './combobox-position';
import type { ComboboxPosition } from './combobox-position';

const sameBox = (a: ComboboxPosition | null, b: ComboboxPosition) =>
  a !== null && a.left === b.left && a.width === b.width && a.maxHeight === b.maxHeight && a.top === b.top && a.bottom === b.bottom;

export const useComboboxPosition = (
  open: boolean,
  anchorRef: RefObject<HTMLElement | null>,
  popupRef: RefObject<HTMLElement | null>,
  contentKey: unknown,
): ComboboxPosition | null => {
  const [position, setPosition] = useState<ComboboxPosition | null>(null);

  const update = useCallback(() => {
    const anchor = anchorRef.current;
    const popup = popupRef.current;
    if (!anchor || !popup) return;
    const next = computeComboboxPosition(
      anchor.getBoundingClientRect(),
      popup.scrollHeight,
      { width: window.innerWidth, height: window.innerHeight },
    );
    setPosition((previous) => (sameBox(previous, next) ? previous : next));
  }, [anchorRef, popupRef]);

  useLayoutEffect(() => {
    if (open) update();
  }, [open, contentKey, update]);

  useEffect(() => {
    if (!open) return;
    window.addEventListener('resize', update);
    window.addEventListener('scroll', update, true);
    window.visualViewport?.addEventListener('resize', update);
    return () => {
      window.removeEventListener('resize', update);
      window.removeEventListener('scroll', update, true);
      window.visualViewport?.removeEventListener('resize', update);
    };
  }, [open, update]);

  return open ? position : null;
};

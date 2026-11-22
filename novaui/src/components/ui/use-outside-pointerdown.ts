import { useEffect } from 'react';
import type { RefObject } from 'react';

export const useOutsidePointerDown = (
  active: boolean,
  refs: RefObject<HTMLElement | null>[],
  onOutside: () => void,
) => {
  useEffect(() => {
    if (!active) return;
    const onDown = (event: PointerEvent) => {
      if (!refs.some((ref) => ref.current?.contains(event.target as Node))) onOutside();
    };
    document.addEventListener('pointerdown', onDown);
    return () => document.removeEventListener('pointerdown', onDown);
  }, [active, refs, onOutside]);
};

import type { Viewport } from './menu-position';

export type ComboboxPosition = {
  left: number;
  width: number;
  maxHeight: number;
  top?: number;
  bottom?: number;
};

const GAP = 4;
const MARGIN = 8;
const MAX_HEIGHT = 320;
const MIN_HEIGHT = 120;

export const computeComboboxPosition = (
  anchor: Pick<DOMRect, 'top' | 'bottom' | 'left' | 'width'>,
  contentHeight: number,
  viewport: Viewport,
): ComboboxPosition => {
  const below = viewport.height - anchor.bottom - GAP - MARGIN;
  const above = anchor.top - GAP - MARGIN;
  const wanted = Math.min(contentHeight, MAX_HEIGHT);
  const goAbove = below < Math.min(wanted, MIN_HEIGHT) && above > below;
  const space = Math.max(goAbove ? above : below, 0);
  const width = Math.min(anchor.width, viewport.width - 2 * MARGIN);
  const left = Math.min(Math.max(anchor.left, MARGIN), viewport.width - MARGIN - width);
  const base = { left, width, maxHeight: Math.min(wanted, space) };
  return goAbove
    ? { ...base, bottom: viewport.height - anchor.top + GAP }
    : { ...base, top: anchor.bottom + GAP };
};

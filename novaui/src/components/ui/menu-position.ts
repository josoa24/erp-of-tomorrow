export type MenuAlign = 'start' | 'end';

export type MenuPosition = { top?: number; bottom?: number; left?: number; right?: number };

export type Viewport = { width: number; height: number };

export const MENU_GAP = 8;
export const VIEWPORT_MARGIN = 16;

export const computeMenuPosition = (
  trigger: Pick<DOMRect, 'top' | 'bottom' | 'left' | 'right'>,
  contentHeight: number,
  viewport: Viewport,
  align: MenuAlign,
): MenuPosition => {
  const fitsBelow = trigger.bottom + MENU_GAP + contentHeight <= viewport.height;
  const fitsAbove = trigger.top - MENU_GAP - contentHeight >= 0;
  const vertical = !fitsBelow && fitsAbove
    ? { bottom: viewport.height - trigger.top + MENU_GAP }
    : { top: trigger.bottom + MENU_GAP };
  const horizontal = align === 'end'
    ? { right: Math.max(VIEWPORT_MARGIN, viewport.width - trigger.right) }
    : { left: Math.max(VIEWPORT_MARGIN, trigger.left) };
  return { ...vertical, ...horizontal };
};

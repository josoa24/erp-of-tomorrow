import { createPortal } from 'react-dom';
import type { CSSProperties, ReactNode, RefObject } from 'react';
import type { ComboboxPosition } from './combobox-position';
import { Spinner } from './spinner';

export type ComboboxPopupProps = {
  popupRef: RefObject<HTMLDivElement | null>;
  listId: string;
  label: string;
  position: ComboboxPosition | null;
  hasItems: boolean;
  loading: boolean;
  emptyText: string;
  children: ReactNode;
};

export function ComboboxPopup({ popupRef, listId, label, position, hasItems, loading, emptyText, children }: ComboboxPopupProps) {
  const style: CSSProperties = position
    ? { ...position }
    : { top: 0, left: 0, visibility: 'hidden' };
  return createPortal(
    <div
      ref={popupRef}
      style={style}
      onMouseDown={(event) => event.preventDefault()}
      className="scroll-thin fixed z-[60] overflow-y-auto overscroll-contain rounded-md bg-surface-solid p-1.5 shadow-elevation-2"
    >
      {hasItems ? (
        <div id={listId} role="listbox" aria-label={label} className="flex flex-col gap-0.5">
          {children}
        </div>
      ) : (
        <p role="status" className="flex min-h-11 items-center gap-2 px-3 text-body text-ink-secondary">
          {loading && <Spinner className="size-4" />}
          {loading ? 'Chargement…' : emptyText}
        </p>
      )}
    </div>,
    document.body,
  );
}

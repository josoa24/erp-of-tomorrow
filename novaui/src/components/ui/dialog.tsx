import { X } from 'lucide-react';
import { useId, useRef } from 'react';
import type { KeyboardEvent, ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { useBodyScrollLock } from '@/hooks/useBodyScrollLock';
import { useFocusTrap } from '@/hooks/useFocusTrap';
import { cn } from '@/lib/utils';
import { dialogVariants } from './dialog-variants';
import { IconButton } from './icon-button';

export type DialogProps = {
  open: boolean;
  onClose: () => void;
  title: string;
  description?: ReactNode;
  icon?: ReactNode;
  size?: 'm' | 'l';
  placement?: 'center' | 'sheet';
  footer?: ReactNode;
  closeLabel?: string;
  className?: string;
  children?: ReactNode;
};

export function Dialog({
  open,
  onClose,
  title,
  description,
  icon,
  size,
  placement = 'center',
  footer,
  closeLabel = 'Fermer',
  className,
  children,
}: DialogProps) {
  const titleId = useId();
  const descriptionId = useId();
  const panelRef = useRef<HTMLDivElement>(null);
  useFocusTrap(panelRef, open);
  useBodyScrollLock(open);

  if (!open) return null;

  const onKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key !== 'Escape') return;
    event.stopPropagation();
    onClose();
  };

  return createPortal(
    <div className={cn('fixed inset-0 z-50 flex p-4', placement === 'sheet' ? 'items-end p-0' : 'items-center justify-center')}>
      <div aria-hidden="true" data-testid="dialog-scrim" className="absolute inset-0 bg-overlay-scrim" onClick={onClose} />
      <div
        ref={panelRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        aria-describedby={description ? descriptionId : undefined}
        tabIndex={-1}
        onKeyDown={onKeyDown}
        className={cn(dialogVariants({ placement, size }), className)}
      >
        <header className="flex items-start gap-4">
          {icon}
          <div className="flex min-w-0 flex-1 flex-col gap-1">
            <h2 id={titleId} className="text-heading text-ink-primary">
              {title}
            </h2>
            {description && (
              <p id={descriptionId} className="text-body-s text-ink-secondary">
                {description}
              </p>
            )}
          </div>
          <IconButton variant="ghost" size="m" aria-label={closeLabel} onClick={onClose}>
            <X />
          </IconButton>
        </header>
        {children}
        {footer && <footer className="flex flex-wrap items-center justify-end gap-3">{footer}</footer>}
      </div>
    </div>,
    document.body,
  );
}

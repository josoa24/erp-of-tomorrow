import { Check } from 'lucide-react';
import { useEffect } from 'react';
import { cn } from '@/lib/utils';

export const TOAST_DURATION_MS = 5000;

export type ToastProps = {
  message: string;
  actionLabel?: string;
  onAction?: () => void;
  onDismiss: () => void;
  duration?: number;
  className?: string;
};

export function Toast({ message, actionLabel, onAction, onDismiss, duration = TOAST_DURATION_MS, className }: ToastProps) {
  useEffect(() => {
    const timer = setTimeout(onDismiss, duration);
    return () => clearTimeout(timer);
  }, [onDismiss, duration]);

  return (
    <div
      role="status"
      className={cn(
        'flex max-w-[calc(100vw-32px)] items-center gap-3 rounded-pill bg-surface-night py-3 pr-5 pl-3 shadow-elevation-2',
        className,
      )}
    >
      <span className="inline-flex size-7 shrink-0 items-center justify-center rounded-pill bg-status-success-vivid">
        <Check aria-hidden="true" className="size-4 text-ink-primary" />
      </span>
      <span className="min-w-0 text-body-strong text-ink-inverse">{message}</span>
      {actionLabel && onAction && (
        <button
          type="button"
          onClick={() => {
            onAction();
            onDismiss();
          }}
          className="shrink-0 rounded-xs text-label text-brand-lilac underline-offset-4 outline-none hover:underline"
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
}

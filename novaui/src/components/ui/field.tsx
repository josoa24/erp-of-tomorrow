import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { helperIdFor } from './field-styles';

export type FieldProps = {
  id: string;
  label: string;
  hideLabel?: boolean;
  helper?: ReactNode;
  aside?: ReactNode;
  className?: string;
  children: ReactNode;
};

export function Field({ id, label, hideLabel = false, helper, aside, className, children }: FieldProps) {
  return (
    <div className={cn('flex w-full flex-col gap-1.5', className)}>
      <label htmlFor={id} className={cn('text-label text-ink-primary', hideLabel && 'sr-only')}>
        {label}
      </label>
      {children}
      {(helper || aside) && (
        <div className="flex items-start justify-between gap-3 text-micro text-ink-tertiary">
          <p id={helper ? helperIdFor(id) : undefined}>{helper}</p>
          {aside}
        </div>
      )}
    </div>
  );
}

import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type KbdProps = ComponentProps<'kbd'> & { inverse?: boolean };

export function Kbd({ className, inverse = false, ...props }: KbdProps) {
  return (
    <kbd
      className={cn(
        'inline-flex h-[26px] shrink-0 items-center rounded-xs border px-2 font-sans text-micro whitespace-nowrap',
        inverse ? 'border-ink-inverse text-ink-inverse' : 'border-line-strong text-ink-tertiary',
        className,
      )}
      {...props}
    />
  );
}

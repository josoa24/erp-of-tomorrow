import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

/** Raccourci clavier (Figma « Kbd »), en Geist Mono. */
export function Kbd({ className, ...props }: ComponentProps<'kbd'>) {
  return (
    <kbd
      className={cn(
        'inline-flex items-center rounded-sm border border-border-default bg-bg-subtle px-2 py-0.5',
        'font-mono text-mono-xs font-medium whitespace-nowrap text-text-secondary',
        className,
      )}
      {...props}
    />
  );
}

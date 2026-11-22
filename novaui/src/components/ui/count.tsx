import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type CountTone = 'night' | 'sunken' | 'solid';

const TONES: Record<CountTone, string> = {
  night: 'bg-surface-night text-ink-inverse text-caption',
  sunken: 'bg-surface-sunken text-ink-secondary text-micro',
  solid: 'bg-surface-solid text-ink-secondary text-micro',
};

export type CountProps = Omit<ComponentProps<'span'>, 'children'> & {
  value: number;
  max?: number;
  tone?: CountTone;
};

export function Count({ className, tone = 'sunken', value, max = 99, ...props }: CountProps) {
  return (
    <span
      className={cn(
        'inline-flex h-5 min-w-5 items-center justify-center rounded-pill px-1.5 whitespace-nowrap',
        TONES[tone],
        className,
      )}
      {...props}
    >
      {value > max ? `${max}+` : value}
    </span>
  );
}

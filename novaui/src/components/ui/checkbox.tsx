import { Check } from 'lucide-react';
import { useId } from 'react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type CheckboxProps = Omit<ComponentProps<'input'>, 'type' | 'size'> & {
  label?: string;
};

export function Checkbox({ id, label, className, disabled, ...props }: CheckboxProps) {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <label
      htmlFor={inputId}
      className={cn(
        'inline-flex min-h-11 cursor-pointer items-center gap-3 text-body text-ink-primary',
        disabled && 'cursor-not-allowed opacity-40',
        className,
      )}
    >
      <span className="relative inline-flex size-[22px] shrink-0">
        <input
          id={inputId}
          type="checkbox"
          disabled={disabled}
          className={cn(
            'peer size-[22px] cursor-[inherit] appearance-none rounded-xs border-[1.5px] border-line-strong bg-surface-solid outline-none',
            'focus-visible:shadow-focus-ring checked:border-surface-night checked:bg-surface-night',
          )}
          {...props}
        />
        <Check
          aria-hidden="true"
          className="pointer-events-none absolute inset-0 m-auto hidden size-4 text-ink-inverse peer-checked:block"
        />
      </span>
      {label}
    </label>
  );
}

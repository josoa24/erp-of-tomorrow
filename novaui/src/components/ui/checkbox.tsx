import { Check } from 'lucide-react';
import { useEffect, useId, useRef } from 'react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type CheckboxProps = Omit<ComponentProps<'input'>, 'type' | 'size'> & {
  label?: string;
  /** Sélection partielle d’un groupe (« Tout sélectionner » partiellement coché). */
  indeterminate?: boolean;
};

/**
 * Case à cocher (Figma « Checkbox ») : case de 16 px, zone cliquable étendue au libellé.
 * Sans `label`, fournir un `aria-label`.
 */
export function Checkbox({
  id,
  label,
  indeterminate = false,
  className,
  disabled,
  ref,
  ...props
}: CheckboxProps) {
  const generatedId = useId();
  const inputId = id ?? generatedId;
  const inputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    if (inputRef.current) inputRef.current.indeterminate = indeterminate;
  }, [indeterminate]);

  return (
    <label
      htmlFor={inputId}
      className={cn(
        'inline-flex cursor-pointer items-center gap-2 text-body-m text-text-primary',
        disabled && 'cursor-not-allowed text-text-disabled',
        className,
      )}
    >
      <span className="relative inline-flex size-4 shrink-0">
        <input
          ref={(node) => {
            inputRef.current = node;
            if (typeof ref === 'function') ref(node);
            else if (ref) ref.current = node;
          }}
          id={inputId}
          type="checkbox"
          disabled={disabled}
          className={cn(
            'peer size-4 cursor-[inherit] appearance-none rounded-sm border border-border-control bg-bg-surface outline-none',
            'focus-visible:shadow-focus-ring',
            'checked:border-transparent checked:bg-bg-primary indeterminate:border-transparent indeterminate:bg-bg-primary',
            'disabled:border-border-default disabled:bg-bg-disabled',
            'disabled:checked:bg-bg-muted disabled:indeterminate:bg-bg-muted',
          )}
          {...props}
        />
        <Check
          aria-hidden="true"
          className={cn(
            'pointer-events-none absolute inset-0 m-auto hidden size-3 text-icon-on-primary [stroke-width:2.5]',
            'peer-checked:block peer-indeterminate:hidden peer-disabled:text-text-disabled',
          )}
        />
        <span
          aria-hidden="true"
          className={cn(
            'pointer-events-none absolute inset-0 m-auto hidden h-0.5 w-2 rounded-[1px] bg-icon-on-primary',
            'peer-indeterminate:block peer-disabled:bg-text-disabled',
          )}
        />
      </span>
      {label}
    </label>
  );
}

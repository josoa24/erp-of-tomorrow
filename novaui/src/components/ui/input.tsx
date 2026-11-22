import { useId } from 'react';
import type { ComponentProps, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { Field } from './field';
import { fieldControlClass, helperIdFor } from './field-styles';

export type InputProps = Omit<ComponentProps<'input'>, 'size'> & {
  label: string;
  hideLabel?: boolean;
  helper?: ReactNode;
  invalid?: boolean;
  trailing?: ReactNode;
  containerClassName?: string;
};

export function Input({
  id,
  label,
  hideLabel,
  helper,
  invalid = false,
  trailing,
  className,
  containerClassName,
  ...props
}: InputProps) {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <Field id={inputId} label={label} hideLabel={hideLabel} helper={helper} className={containerClassName}>
      <div className={cn(fieldControlClass, 'h-12', trailing && 'pr-2')}>
        <input
          id={inputId}
          aria-invalid={invalid || undefined}
          aria-describedby={helper ? helperIdFor(inputId) : undefined}
          className={cn(
            'min-w-0 flex-1 bg-transparent text-body text-ink-primary outline-none placeholder:text-ink-tertiary',
            className,
          )}
          {...props}
        />
        {trailing}
      </div>
    </Field>
  );
}

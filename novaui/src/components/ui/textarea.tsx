import { useId } from 'react';
import type { ComponentProps, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { Field } from './field';
import { fieldControlClass, helperIdFor } from './field-styles';

export type TextareaProps = ComponentProps<'textarea'> & {
  label: string;
  hideLabel?: boolean;
  helper?: ReactNode;
  invalid?: boolean;
  containerClassName?: string;
};

const lengthOf = (value: TextareaProps['value']): number => (typeof value === 'string' ? value.length : 0);

export function Textarea({
  id,
  label,
  hideLabel,
  helper,
  invalid = false,
  maxLength,
  value,
  rows = 4,
  className,
  containerClassName,
  ...props
}: TextareaProps) {
  const generatedId = useId();
  const textareaId = id ?? generatedId;
  const counter = maxLength ? (
    <span aria-live="polite" className="shrink-0 tabular-nums">
      {lengthOf(value)} / {maxLength}
    </span>
  ) : undefined;

  return (
    <Field
      id={textareaId}
      label={label}
      hideLabel={hideLabel}
      helper={helper}
      aside={counter}
      className={containerClassName}
    >
      <div className={cn(fieldControlClass, 'py-3')}>
        <textarea
          id={textareaId}
          rows={rows}
          value={value}
          maxLength={maxLength}
          aria-invalid={invalid || undefined}
          aria-describedby={helper ? helperIdFor(textareaId) : undefined}
          className={cn(
            'min-w-0 flex-1 resize-y bg-transparent text-body text-ink-primary outline-none placeholder:text-ink-tertiary',
            className,
          )}
          {...props}
        />
      </div>
    </Field>
  );
}

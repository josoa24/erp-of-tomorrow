import { CircleAlert } from 'lucide-react';
import { useId } from 'react';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export type InputProps = Omit<ComponentProps<'input'>, 'size'> & {
  /** Libellé toujours visible : le placeholder ne sert qu’à donner un exemple (« ex : … »). */
  label: string;
  /** Masque visuellement le libellé tout en le gardant pour les lecteurs d’écran. */
  hideLabel?: boolean;
  helper?: string;
  /** Message d’erreur qui dit quoi faire. Sa présence passe le champ en état Error. */
  error?: string;
  containerClassName?: string;
};

/** Champ texte (Figma « Input ») : libellé au-dessus, aide ou erreur en dessous. */
export function Input({
  id,
  label,
  hideLabel = false,
  helper,
  error,
  disabled,
  className,
  containerClassName,
  ...props
}: InputProps) {
  const generatedId = useId();
  const inputId = id ?? generatedId;
  const messageId = `${inputId}-message`;
  const message = error ?? helper;

  return (
    <div className={cn('flex w-full flex-col gap-2', containerClassName)}>
      <label
        htmlFor={inputId}
        className={cn(
          'text-body-s font-medium text-text-primary',
          disabled && 'text-text-disabled',
          hideLabel && 'sr-only',
        )}
      >
        {label}
      </label>

      <div
        className={cn(
          'flex h-(--layout-control-height) w-full items-center gap-2 rounded-md border bg-bg-surface px-3 transition-colors',
          'border-border-control hover:border-icon-muted',
          'focus-within:border-[1.5px] focus-within:border-border-primary focus-within:shadow-focus-ring',
          error &&
            'border-[1.5px] border-border-danger hover:border-border-danger focus-within:border-border-danger',
          disabled && 'pointer-events-none border-border-default bg-bg-disabled',
        )}
      >
        <input
          id={inputId}
          disabled={disabled}
          aria-invalid={error ? true : undefined}
          aria-describedby={message ? messageId : undefined}
          className={cn(
            'min-w-0 flex-1 bg-transparent text-body-m text-text-primary outline-none',
            'placeholder:text-text-tertiary disabled:text-text-disabled disabled:placeholder:text-text-disabled',
            className,
          )}
          {...props}
        />
        {error && <CircleAlert aria-hidden="true" className="size-5 shrink-0 text-icon-danger" />}
      </div>

      {message && (
        <p
          id={messageId}
          className={cn(
            'text-body-s text-text-secondary',
            error && 'text-text-danger',
            disabled && 'text-text-disabled',
          )}
        >
          {message}
        </p>
      )}
    </div>
  );
}

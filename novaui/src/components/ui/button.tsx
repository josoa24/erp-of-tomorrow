import type { VariantProps } from 'class-variance-authority';
import type { ComponentProps, MouseEvent, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { buttonVariants } from './button-variants';
import { Spinner } from './spinner';

export const BUTTON_LOADING_LABEL = 'Exécution…';

export type ButtonVariant = NonNullable<VariantProps<typeof buttonVariants>['variant']>;
export type ButtonSize = NonNullable<VariantProps<typeof buttonVariants>['size']>;

export type ButtonProps = ComponentProps<'button'> &
  VariantProps<typeof buttonVariants> & {
    leadingIcon?: ReactNode;
    trailingIcon?: ReactNode;
    loading?: boolean;
    loadingLabel?: string;
  };

export function Button({
  className,
  variant,
  size,
  leadingIcon,
  trailingIcon,
  loading = false,
  loadingLabel = BUTTON_LOADING_LABEL,
  disabled,
  type = 'button',
  children,
  onClick,
  ...props
}: ButtonProps) {
  const handleClick = (event: MouseEvent<HTMLButtonElement>) => {
    if (loading) {
      event.preventDefault();
      return;
    }
    onClick?.(event);
  };

  return (
    <button
      type={type}
      disabled={disabled}
      aria-disabled={disabled || loading || undefined}
      aria-busy={loading || undefined}
      onClick={handleClick}
      className={cn(buttonVariants({ variant, size }), className)}
      {...props}
    >
      {loading ? <Spinner /> : leadingIcon}
      {loading ? loadingLabel : children}
      {!loading && trailingIcon}
    </button>
  );
}

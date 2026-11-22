import type { VariantProps } from 'class-variance-authority';
import type { ComponentProps, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { buttonVariants } from './button-variants';
import { Spinner } from './spinner';

export type ButtonProps = ComponentProps<'button'> &
  VariantProps<typeof buttonVariants> & {
    leadingIcon?: ReactNode;
    trailingIcon?: ReactNode;
    /** Bloque l’action, affiche le spinner à la place de l’icône et conserve la largeur. */
    loading?: boolean;
  };

export function Button({
  className,
  variant,
  size,
  leadingIcon,
  trailingIcon,
  loading = false,
  disabled,
  type = 'button',
  children,
  onClick,
  ...props
}: ButtonProps) {
  return (
    <button
      type={type}
      disabled={disabled}
      aria-busy={loading || undefined}
      onClick={loading ? undefined : onClick}
      className={cn(buttonVariants({ variant, size }), className)}
      {...props}
    >
      {loading ? <Spinner /> : leadingIcon}
      {children}
      {!loading && trailingIcon}
    </button>
  );
}

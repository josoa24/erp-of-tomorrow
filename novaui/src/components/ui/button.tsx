import { cva } from 'class-variance-authority';
import type { VariantProps } from 'class-variance-authority';
import { Sparkle } from 'lucide-react';
import type { ComponentProps, ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { Spinner } from './spinner';

/**
 * Bouton d’action (Figma « Button »).
 * - primary : une seule action principale par zone
 * - secondary : actions courantes
 * - ghost : actions tertiaires, neutre
 * - danger : action destructive confirmée
 * Tailles : m = layout/control-height (40 Desktop, 44 Mobile), s = 32 (barres d’outils, tableaux).
 */
export const buttonVariants = cva(
  [
    'inline-flex shrink-0 items-center justify-center whitespace-nowrap rounded-md font-medium',
    'outline-none transition-colors focus-visible:shadow-focus-ring',
    'disabled:pointer-events-none disabled:bg-bg-disabled disabled:text-text-disabled disabled:[&_svg]:text-text-disabled',
    'aria-busy:pointer-events-none',
    '[&_svg]:size-4 [&_svg]:shrink-0',
  ],
  {
    variants: {
      variant: {
        primary: [
          'bg-bg-primary text-text-on-primary [&_svg]:text-icon-on-primary',
          'hover:bg-bg-primary-hover active:bg-bg-primary-pressed',
        ],
        secondary: [
          'border border-border-strong bg-bg-surface text-text-primary [&_svg]:text-icon-default',
          'hover:bg-bg-hover active:bg-bg-pressed disabled:border-border-default',
        ],
        ghost: [
          'text-text-secondary [&_svg]:text-icon-muted',
          'hover:bg-bg-hover hover:text-text-primary hover:[&_svg]:text-icon-default',
          'focus-visible:bg-bg-hover active:bg-bg-pressed active:text-text-primary',
          'disabled:bg-transparent',
        ],
        danger: [
          'bg-bg-danger text-text-on-primary [&_svg]:text-icon-on-primary',
          'hover:bg-bg-danger-hover active:bg-bg-danger-pressed',
        ],
      },
      size: {
        m: 'h-(--layout-control-height) gap-2 px-4 text-body-m',
        s: 'h-8 gap-1 px-3 text-button-s',
      },
    },
    defaultVariants: { variant: 'primary', size: 'm' },
  },
);

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

export type AiButtonProps = Omit<ButtonProps, 'variant' | 'leadingIcon'>;

/**
 * Accès à l’assistant IA (Figma « Button/AI »). Traité comme un bouton secondaire :
 * seule l’étoile signale l’IA. Ne remplace jamais l’action principale.
 */
export function AiButton({
  className,
  size,
  children = 'Demander à l’IA',
  ...props
}: AiButtonProps) {
  return (
    <Button
      variant="secondary"
      size={size}
      leadingIcon={<Sparkle aria-hidden="true" className="fill-current" />}
      className={cn(
        '[&_svg]:text-icon-ai',
        size === 's' ? '[&_svg]:size-4' : '[&_svg]:size-5',
        className,
      )}
      {...props}
    >
      {children}
    </Button>
  );
}

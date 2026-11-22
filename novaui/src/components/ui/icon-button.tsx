import type { VariantProps } from 'class-variance-authority';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import { iconButtonVariants } from './icon-button-variants';

export type IconButtonProps = ComponentProps<'button'> &
  VariantProps<typeof iconButtonVariants> & {
    /** Libellé accessible obligatoire : le bouton n’a pas de texte visible. */
    'aria-label': string;
  };

export function IconButton({ className, variant, size, type = 'button', ...props }: IconButtonProps) {
  return (
    <button
      type={type}
      className={cn(iconButtonVariants({ variant, size }), className)}
      {...props}
    />
  );
}

import { Sparkle } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from './button';
import type { ButtonProps } from './button';

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

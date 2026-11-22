import { Sparkles } from 'lucide-react';
import { Button } from './button';
import type { ButtonProps } from './button';

export type AiButtonProps = Omit<ButtonProps, 'variant' | 'leadingIcon'>;

export function AiButton({ children = 'Demander à l’IA', ...props }: AiButtonProps) {
  return (
    <Button variant="eclipse" leadingIcon={<Sparkles aria-hidden="true" />} {...props}>
      {children}
    </Button>
  );
}

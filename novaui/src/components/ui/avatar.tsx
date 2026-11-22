import { cva } from 'class-variance-authority';
import type { VariantProps } from 'class-variance-authority';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

const avatarVariants = cva(
  'inline-flex shrink-0 items-center justify-center rounded-full bg-bg-primary-subtle font-medium whitespace-nowrap text-text-link select-none',
  {
    variants: {
      size: {
        s: 'size-6 font-mono text-mono-xs',
        m: 'size-8 text-caption',
        l: 'size-10 text-body-s',
      },
    },
    defaultVariants: { size: 's' },
  },
);

export type AvatarProps = Omit<ComponentProps<'span'>, 'children'> &
  VariantProps<typeof avatarVariants> & {
    /** Nom complet de la personne ou de l’entreprise ; les initiales en sont dérivées. */
    name: string;
  };

function getInitials(name: string): string {
  const words = name.trim().split(/\s+/).filter(Boolean);
  const first = words[0]?.[0] ?? '';
  const last = words.length > 1 ? (words[words.length - 1]?.[0] ?? '') : '';
  return (first + last).toUpperCase();
}

/**
 * Avatar à initiales (Figma « Avatar »). Toujours accompagné du nom à côté
 * ou en infobulle ; le nom sert aussi de libellé accessible.
 */
export function Avatar({ className, size, name, ...props }: AvatarProps) {
  return (
    <span role="img" aria-label={name} className={cn(avatarVariants({ size }), className)} {...props}>
      {getInitials(name)}
    </span>
  );
}

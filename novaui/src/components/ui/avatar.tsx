import type { VariantProps } from 'class-variance-authority';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import { toInitials } from '@/utils/initials';
import { avatarVariants } from './avatar-variants';

export type AvatarProps = Omit<ComponentProps<'span'>, 'children'> &
  VariantProps<typeof avatarVariants> & {
    name: string;
    initials?: string;
    decorative?: boolean;
  };

export function Avatar({ className, size, tone, name, initials, decorative = false, ...props }: AvatarProps) {
  return (
    <span
      role={decorative ? undefined : 'img'}
      aria-label={decorative ? undefined : name}
      aria-hidden={decorative || undefined}
      className={cn(avatarVariants({ size, tone }), className)}
      {...props}
    >
      {initials ?? toInitials(name)}
    </span>
  );
}

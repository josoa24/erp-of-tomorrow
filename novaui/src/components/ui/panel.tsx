import type { VariantProps } from 'class-variance-authority';
import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';
import { panelVariants } from './panel-variants';

export type PanelProps = ComponentProps<'section'> & VariantProps<typeof panelVariants>;

export function Panel({ className, tone, radius, padding, ...props }: PanelProps) {
  return <section className={cn(panelVariants({ tone, radius, padding }), className)} {...props} />;
}

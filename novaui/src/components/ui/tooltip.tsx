import { cloneElement, useId } from 'react';
import type { ReactElement, ReactNode } from 'react';
import { cn } from '@/lib/utils';

export type TooltipPlacement = 'top' | 'bottom' | 'left' | 'right';

export type TooltipProps = {
  children: ReactElement<{ 'aria-describedby'?: string }>;
  content: ReactNode;
  title?: string;
  placement?: TooltipPlacement;
  className?: string;
};

const bubblePosition: Record<TooltipPlacement, string> = {
  top: 'bottom-full left-1/2 mb-1.5 -translate-x-1/2',
  bottom: 'top-full left-1/2 mt-1.5 -translate-x-1/2',
  left: 'right-full top-1/2 mr-1.5 -translate-y-1/2',
  right: 'left-full top-1/2 ml-1.5 -translate-y-1/2',
};

export function Tooltip({ children, content, title, placement = 'top', className }: TooltipProps) {
  const id = useId();

  return (
    <span className="group/tooltip relative inline-flex">
      {cloneElement(children, { 'aria-describedby': id })}
      <span
        id={id}
        role="tooltip"
        className={cn(
          'pointer-events-none invisible absolute z-50 opacity-0 transition-[opacity,visibility] duration-150',
          'group-hover/tooltip:visible group-hover/tooltip:opacity-100 group-hover/tooltip:delay-300',
          'group-focus-within/tooltip:visible group-focus-within/tooltip:opacity-100 group-focus-within/tooltip:delay-300',
          bubblePosition[placement],
        )}
      >
        <span
          className={cn(
            'flex w-max max-w-60 flex-col gap-0.5 rounded-sm bg-surface-night px-3 py-2 text-body-s text-ink-inverse shadow-elevation-2',
            className,
          )}
        >
          {title && <span className="text-label">{title}</span>}
          <span>{content}</span>
        </span>
      </span>
    </span>
  );
}

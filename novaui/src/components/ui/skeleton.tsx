import type { ComponentProps } from 'react';
import { cn } from '@/lib/utils';

export function Skeleton({ className, ...props }: ComponentProps<'span'>) {
  return (
    <span
      aria-hidden="true"
      className={cn('block h-4 animate-pulse rounded-xs bg-surface-sunken motion-reduce:animate-none', className)}
      {...props}
    />
  );
}

export type TableSkeletonProps = {
  rows?: number;
  columns?: number;
  label?: string;
  className?: string;
};

export function TableSkeleton({ rows = 5, columns = 4, label = 'Chargement…', className }: TableSkeletonProps) {
  return (
    <div role="status" aria-label={label} className={cn('flex flex-col', className)}>
      <div className="flex h-12 items-center gap-4 bg-surface-sunken px-4">
        {Array.from({ length: columns }, (_, column) => (
          <Skeleton key={column} className="h-3 flex-1 bg-line-subtle" />
        ))}
      </div>
      {Array.from({ length: rows }, (_, row) => (
        <div key={row} className="flex h-[52px] items-center gap-4 border-b border-line-subtle px-4 last:border-b-0">
          {Array.from({ length: columns }, (_, column) => (
            <Skeleton key={column} className="flex-1" />
          ))}
        </div>
      ))}
    </div>
  );
}

import type { ReactNode } from 'react'

export interface FilterBarProps {
  children: ReactNode
  sortHint?: string
  label?: string
}

export const FilterBar = ({ children, sortHint, label = 'Filtres' }: FilterBarProps) => (
  <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between sm:gap-4">
    <div role="group" aria-label={label} className="-m-1 flex min-w-0 gap-2 overflow-x-auto p-1">
      {children}
    </div>
    {sortHint && <p className="shrink-0 text-micro text-ink-tertiary">{sortHint}</p>}
  </div>
)

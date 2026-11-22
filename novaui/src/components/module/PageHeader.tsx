import type { ReactNode } from 'react'
import { SearchField, type SearchFieldProps } from '../ui/search-field'

export interface PageHeaderProps {
  title: string
  subtitle?: ReactNode
  search?: SearchFieldProps
  primaryAction?: ReactNode
}

export const PageHeader = ({ title, subtitle, search, primaryAction }: PageHeaderProps) => (
  <header className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between md:pl-2">
    <div className="flex min-w-0 flex-col gap-1">
      <h1 className="text-display-m break-words text-ink-primary max-md:text-title">{title}</h1>
      {subtitle && <p className="text-body-s text-ink-tertiary">{subtitle}</p>}
    </div>
    {(search || primaryAction) && (
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center md:shrink-0">
        {search && <SearchField containerClassName="sm:flex-1 md:w-[340px] md:flex-none" {...search} />}
        {primaryAction}
      </div>
    )}
  </header>
)

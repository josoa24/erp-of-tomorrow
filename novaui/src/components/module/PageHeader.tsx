import type { ReactNode } from 'react'
import { SearchField, type SearchFieldProps } from '../ui/search-field'

export interface PageHeaderProps {
  title: string
  subtitle?: ReactNode
  search?: SearchFieldProps & { resetKey?: number }
  primaryAction?: ReactNode
}

const SEARCH_CLASS = 'sm:flex-1 md:w-[340px] md:min-w-0 md:flex-initial'

const renderSearch = ({ resetKey, ...props }: NonNullable<PageHeaderProps['search']>) => (
  <SearchField key={resetKey} containerClassName={SEARCH_CLASS} {...props} />
)

export const PageHeader = ({ title, subtitle, search, primaryAction }: PageHeaderProps) => (
  <header className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between md:pl-2">
    <div className="flex min-w-0 flex-col gap-1 md:min-w-[auto]">
      <h1 className="text-display-m break-words text-ink-primary max-md:text-title md:whitespace-nowrap">{title}</h1>
      {subtitle && <p className="text-body-s text-ink-tertiary">{subtitle}</p>}
    </div>
    {(search || primaryAction) && (
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center md:min-w-0">
        {search && renderSearch(search)}
        {primaryAction}
      </div>
    )}
  </header>
)

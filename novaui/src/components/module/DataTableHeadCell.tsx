import { ArrowDown, ArrowUp } from 'lucide-react'
import { cn } from '@/lib/utils'
import { alignClass, widthStyle } from './cellStyles'
import { NewTag } from './NewTag'
import type { DataTableColumn } from './types'

interface DataTableHeadCellProps<Row> {
  column: DataTableColumn<Row>
  isFirst: boolean
}

const SortIcon = ({ sort }: { sort?: DataTableColumn<unknown>['sort'] }) => {
  if (sort === 'ascending') return <ArrowUp aria-hidden="true" className="size-3.5" />
  if (sort === 'descending') return <ArrowDown aria-hidden="true" className="size-3.5" />
  return null
}

export const DataTableHeadCell = <Row,>({ column, isFirst }: DataTableHeadCellProps<Row>) => {
  const content = (
    <span className="inline-flex items-center gap-2">
      {column.header}
      {column.isNew && <NewTag />}
      <SortIcon sort={column.sort} />
    </span>
  )

  return (
    <th
      scope="col"
      aria-sort={column.sort}
      style={widthStyle(column.width)}
      className={cn(
        'h-12 px-4 text-caption font-medium whitespace-nowrap text-ink-tertiary uppercase',
        alignClass(column.align),
        isFirst ? 'sticky left-0 z-[1] bg-surface-sunken' : 'bg-surface-sunken',
        column.isNew && 'bg-status-success-bg',
      )}
    >
      {column.onSort ? (
        <button type="button" onClick={column.onSort} className="rounded-xs uppercase outline-none">
          {content}
        </button>
      ) : (
        content
      )}
    </th>
  )
}

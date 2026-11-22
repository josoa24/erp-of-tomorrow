import { cn } from '@/lib/utils'
import { NewTag } from './NewTag'
import { RowActionsMenu } from './RowActionsMenu'
import type { DataTableLayoutProps } from './types'

export const DataTableCards = <Row,>({
  caption,
  columns,
  rows,
  getRowKey,
  getRowLabel,
  rowActions,
}: DataTableLayoutProps<Row>) => {
  const [lead, ...details] = columns

  return (
    <ul aria-label={caption} className="flex flex-col gap-3">
      {rows.map((row) => (
        <li key={getRowKey(row)} className="flex flex-col gap-3 rounded-lg bg-surface-solid p-4">
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0 pt-2.5 text-label text-ink-primary">{lead?.render(row)}</div>
            {rowActions && <RowActionsMenu rowLabel={getRowLabel(row)} actions={rowActions(row)} />}
          </div>
          <dl className="grid grid-cols-[minmax(0,auto)_minmax(0,1fr)] gap-x-4 gap-y-2">
            {details.map((column) => (
              <div key={column.key} className="contents">
                <dt className="flex items-center gap-2 text-caption text-ink-tertiary uppercase">
                  {column.header}
                  {column.isNew && <NewTag />}
                </dt>
                <dd
                  className={cn(
                    'min-w-0 text-body-s break-words text-ink-secondary',
                    column.isNew && 'rounded-xs bg-status-success-bg px-1.5 text-status-success-fg',
                  )}
                >
                  {column.render(row)}
                </dd>
              </div>
            ))}
          </dl>
        </li>
      ))}
    </ul>
  )
}

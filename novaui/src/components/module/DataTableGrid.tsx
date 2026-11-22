import { bodyCellClass, widthStyle } from './cellStyles'
import { DataTableHeadCell } from './DataTableHeadCell'
import { RowActionsMenu } from './RowActionsMenu'
import type { DataTableLayoutProps } from './types'

export const DataTableGrid = <Row,>({
  caption,
  columns,
  rows,
  getRowKey,
  getRowLabel,
  rowActions,
}: DataTableLayoutProps<Row>) => (
  <div className="overflow-hidden rounded-lg bg-surface-solid">
    <div className="scroll-thin relative overflow-x-auto overscroll-x-contain">
    <table className="w-full border-collapse">
      <caption className="sr-only">{caption}</caption>
      <thead>
        <tr>
          {columns.map((column, index) => (
            <DataTableHeadCell key={column.key} column={column} isFirst={index === 0} />
          ))}
          {rowActions && (
            <th scope="col" className="h-12 w-14 bg-surface-sunken px-4">
              <span className="sr-only">Actions</span>
            </th>
          )}
        </tr>
      </thead>
      <tbody>
        {rows.map((row) => (
          <tr key={getRowKey(row)} className="group border-b border-line-subtle last:border-b-0 hover:bg-surface-sunken">
            {columns.map((column, index) => (
              <td
                key={column.key}
                style={widthStyle(column.width)}
                className={bodyCellClass(index === 0, column.isNew, column.align)}
              >
                {column.render(row)}
              </td>
            ))}
            {rowActions && (
              <td className="w-14 px-1.5 text-center">
                <RowActionsMenu rowLabel={getRowLabel(row)} actions={rowActions(row)} />
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
    </div>
  </div>
)

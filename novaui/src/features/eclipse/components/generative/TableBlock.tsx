import { DataTable } from '../../../../components/module/DataTable'
import type { DataTableColumn } from '../../../../components/module/types'
import { Panel } from '../../../../components/ui/panel'
import { StatusPill } from '../../../../components/ui/status-pill'
import { formatCell, isStatusCell } from '../../services/blockFormat'
import type { UiCell, UiColumn } from '../../types/uiBlock'

type Row = Record<string, UiCell>

const toColumn = (column: UiColumn): DataTableColumn<Row> => ({
  key: column.key,
  header: column.label,
  align: column.kind === 'number' || column.kind === 'money' ? 'end' : 'start',
  render: (row) => {
    const cell = row[column.key] ?? null
    return isStatusCell(cell) ? <StatusPill label={cell.label} tone={cell.tone} /> : formatCell(cell, column.kind)
  },
})

interface TableBlockProps {
  title: string
  columns: UiColumn[]
  rows: Row[]
  total?: number
}

export const TableBlock = ({ title, columns, rows, total }: TableBlockProps) => (
  <Panel aria-label={title} padding="s" className="flex flex-col gap-2 bg-surface-solid">
    <h4 className="px-2 pt-1 text-label text-ink-secondary">{title}</h4>
    <DataTable
      caption={title}
      columns={columns.map(toColumn)}
      rows={rows}
      getRowKey={(row) => JSON.stringify(row)}
      getRowLabel={(row) => formatCell(row[columns[0]?.key ?? ''] ?? null, 'text')}
    />
    {total !== undefined && total > rows.length && (
      <p className="px-2 pb-1 text-micro text-ink-tertiary">{`${rows.length} affichés sur ${total}`}</p>
    )}
  </Panel>
)

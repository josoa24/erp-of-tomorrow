import type { LucideIcon } from 'lucide-react'
import type { ReactNode } from 'react'
import type { StatProps } from '../ui/stat'

export type ColumnAlign = 'start' | 'center' | 'end'
export type SortDirection = 'ascending' | 'descending' | 'none'

export interface DataTableColumn<Row> {
  key: string
  header: string
  render: (row: Row) => ReactNode
  align?: ColumnAlign
  width?: number | string
  isNew?: boolean
  sort?: SortDirection
  onSort?: () => void
}

export interface RowAction {
  key: string
  label: string
  icon?: LucideIcon
  tone?: 'default' | 'danger'
  onSelect: () => void
}

export type KpiItem = StatProps & { key: string }

export interface DataTableProps<Row> {
  caption: string
  columns: DataTableColumn<Row>[]
  rows: Row[]
  getRowKey: (row: Row) => string | number
  getRowLabel: (row: Row) => string
  rowActions?: (row: Row) => RowAction[]
  loading?: boolean
  empty?: ReactNode
  className?: string
}

export type DataTableLayoutProps<Row> = Omit<DataTableProps<Row>, 'loading' | 'empty' | 'className'>

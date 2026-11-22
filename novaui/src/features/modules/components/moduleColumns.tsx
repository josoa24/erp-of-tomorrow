import type { DataTableColumn } from '../../../components/module/types'
import { formatCell } from '../services/recordFormat'
import type { ModuleDefinition } from '../types/moduleDefinition'
import type { ModuleRecord } from '../types/moduleRecord'
import { RecordStatusPill } from './RecordStatusPill'

export const moduleColumns = (definition: ModuleDefinition): DataTableColumn<ModuleRecord>[] => [
  ...definition.columns
    .filter((column) => column.listed !== false)
    .map((column) => ({
      key: column.key,
      header: column.label,
      align: column.type === 'MONEY' || column.type === 'NUMBER' ? ('end' as const) : undefined,
      render: (record: ModuleRecord) => formatCell(column, record.values[column.key]),
    })),
  {
    key: 'status',
    header: 'Statut',
    width: 170,
    render: (record) => <RecordStatusPill status={definition.statuses.find((status) => status.key === record.status)} />,
  },
]

import { useBreakpoint } from '@/hooks/useBreakpoint'
import { cn } from '@/lib/utils'
import { TableSkeleton } from '../ui/skeleton'
import { DataTableCards } from './DataTableCards'
import { DataTableGrid } from './DataTableGrid'
import type { DataTableProps } from './types'

export const DataTable = <Row,>({ loading = false, empty, className, ...layout }: DataTableProps<Row>) => {
  const breakpoint = useBreakpoint()

  if (loading) {
    return (
      <TableSkeleton
        columns={Math.min(layout.columns.length, 5)}
        className={cn('overflow-hidden rounded-lg bg-surface-solid', className)}
      />
    )
  }
  if (layout.rows.length === 0 && empty) {
    return <div className={cn('rounded-lg bg-surface-solid', className)}>{empty}</div>
  }
  return (
    <div className={className}>
      {breakpoint === 'mobile' ? <DataTableCards {...layout} /> : <DataTableGrid {...layout} />}
    </div>
  )
}

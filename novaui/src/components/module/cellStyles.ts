import { cn } from '@/lib/utils'
import type { ColumnAlign } from './types'

const ALIGN: Record<ColumnAlign, string> = {
  start: 'text-left',
  center: 'text-center',
  end: 'text-right',
}

export const alignClass = (align: ColumnAlign = 'start'): string => ALIGN[align]

export const bodyCellClass = (isFirst: boolean, isNew = false, align?: ColumnAlign): string =>
  cn(
    'h-[52px] px-4 whitespace-nowrap',
    alignClass(align),
    isFirst ? 'text-label text-ink-primary' : 'text-body-s text-ink-secondary',
    isFirst && 'sticky left-0 z-[1] bg-surface-solid group-hover:bg-surface-sunken',
    isNew && 'bg-status-success-bg text-status-success-fg',
  )

export const widthStyle = (width?: number | string) => (width === undefined ? undefined : { width })

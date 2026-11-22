import { ArrowRight } from 'lucide-react'
import type { RowAction } from '../../../components/module/types'
import type { ModuleDefinition, ModuleStatus } from '../types/moduleDefinition'
import type { ModuleRecord } from '../types/moduleRecord'

export const nextStatus = (definition: ModuleDefinition, record: ModuleRecord): ModuleStatus | undefined => {
  const index = definition.statuses.findIndex((status) => status.key === record.status)
  const current = definition.statuses[index]
  return current && !current.final ? definition.statuses[index + 1] : undefined
}

export const recordActions =
  (definition: ModuleDefinition, onAdvance: (record: ModuleRecord, next: ModuleStatus) => void) =>
  (record: ModuleRecord): RowAction[] => {
    const next = nextStatus(definition, record)
    return next
      ? [{ key: 'advance', label: `Passer à « ${next.label} »`, icon: ArrowRight, onSelect: () => onAdvance(record, next) }]
      : []
  }

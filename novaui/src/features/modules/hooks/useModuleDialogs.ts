import { useCallback, useState } from 'react'
import type { ModuleStatus } from '../types/moduleDefinition'
import type { ModuleRecord } from '../types/moduleRecord'

export type ModuleDialog =
  | { type: 'create' }
  | { type: 'advance'; record: ModuleRecord; next: ModuleStatus }
  | { type: 'delete' }

export const useModuleDialogs = () => {
  const [dialog, setDialog] = useState<ModuleDialog | null>(null)

  const openCreate = useCallback(() => setDialog({ type: 'create' }), [])
  const openAdvance = useCallback((record: ModuleRecord, next: ModuleStatus) => setDialog({ type: 'advance', record, next }), [])
  const openDelete = useCallback(() => setDialog({ type: 'delete' }), [])
  const close = useCallback(() => setDialog(null), [])

  return { dialog, openCreate, openAdvance, openDelete, close }
}

import { useCallback, useState } from 'react'
import type { StockStatusItem } from '../types/stock'

export type StockDialog =
  | { kind: 'entry'; item: StockStatusItem | null }
  | { kind: 'exit'; item: StockStatusItem }
  | { kind: 'lots'; item: StockStatusItem }
  | null

export const useStockDialogs = () => {
  const [dialog, setDialog] = useState<StockDialog>(null)

  const openEntry = useCallback((item: StockStatusItem | null = null) => setDialog({ kind: 'entry', item }), [])
  const openExit = useCallback((item: StockStatusItem) => setDialog({ kind: 'exit', item }), [])
  const openLots = useCallback((item: StockStatusItem) => setDialog({ kind: 'lots', item }), [])
  const close = useCallback(() => setDialog(null), [])

  return { dialog, openEntry, openExit, openLots, close }
}

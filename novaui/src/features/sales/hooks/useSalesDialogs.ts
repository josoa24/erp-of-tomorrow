import { useCallback, useState } from 'react'
import type { Sale } from '../types/sale'

export type SalesDialog =
  | { kind: 'form'; sale: Sale | null }
  | { kind: 'collect'; sale: Sale }
  | { kind: 'cancel'; sale: Sale }
  | { kind: 'detail'; sale: Sale }
  | null

export const useSalesDialogs = () => {
  const [dialog, setDialog] = useState<SalesDialog>(null)

  return {
    dialog,
    openForm: useCallback((sale: Sale | null = null) => setDialog({ kind: 'form', sale }), []),
    openCollect: useCallback((sale: Sale) => setDialog({ kind: 'collect', sale }), []),
    openCancel: useCallback((sale: Sale) => setDialog({ kind: 'cancel', sale }), []),
    openDetail: useCallback((sale: Sale) => setDialog({ kind: 'detail', sale }), []),
    close: useCallback(() => setDialog(null), []),
  }
}

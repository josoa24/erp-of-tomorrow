import { Layers, PackageMinus, PackagePlus } from 'lucide-react'
import type { RowAction } from '../../../components/module/types'
import type { StockStatusItem } from '../types/stock'

export interface StockRowHandlers {
  onEntry: (item: StockStatusItem) => void
  onExit: (item: StockStatusItem) => void
  onLots: (item: StockStatusItem) => void
}

export const buildStockRowActions = (
  item: StockStatusItem,
  handlers: StockRowHandlers,
  withLots: boolean,
): RowAction[] => [
  { key: 'entry', label: 'Entrée de stock', icon: PackagePlus, onSelect: () => handlers.onEntry(item) },
  ...(item.quantity > 0
    ? [{ key: 'exit', label: 'Sortie de stock', icon: PackageMinus, onSelect: () => handlers.onExit(item) }]
    : []),
  ...(withLots ? [{ key: 'lots', label: 'Voir les lots', icon: Layers, onSelect: () => handlers.onLots(item) }] : []),
]

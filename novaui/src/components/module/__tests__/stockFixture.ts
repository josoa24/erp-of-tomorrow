import { Eye, Trash2 } from 'lucide-react'
import type { DataTableColumn, RowAction } from '../types'

export interface ProductRow {
  id: number
  name: string
  category: string
  quantity: string
  lot: string
}

export const PRODUCTS: ProductRow[] = [
  { id: 1, name: 'Bougies (paquet de 6)', category: 'Maison', quantity: '4 u.', lot: '—' },
  { id: 2, name: 'Huile 5 L', category: 'Épicerie sèche', quantity: '18 u.', lot: 'LOT-2610-C' },
]

export const COLUMNS: DataTableColumn<ProductRow>[] = [
  { key: 'name', header: 'Produit', render: (row) => row.name, width: 420 },
  { key: 'category', header: 'Catégorie', render: (row) => row.category, sort: 'ascending' },
  { key: 'quantity', header: 'Stock', render: (row) => row.quantity, align: 'end' },
  { key: 'lot', header: 'N° de lot', render: (row) => row.lot, isNew: true },
]

export const actionsFor = (onView: (row: ProductRow) => void) => (row: ProductRow): RowAction[] => [
  { key: 'view', label: 'Voir', icon: Eye, onSelect: () => onView(row) },
  { key: 'delete', label: 'Supprimer', icon: Trash2, tone: 'danger', onSelect: () => undefined },
]

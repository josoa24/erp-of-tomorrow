import type { DataTableColumn } from '../../../components/module/types'
import type { StockAdaptationView } from '../services/stockAdaptation'
import { formatIsoDate } from '../services/stockDates'
import { formatStockQuantity } from '../services/stockQuantity'
import type { StockStatusItem } from '../types/stock'
import { StockStatusPill } from './StockStatusPill'

type StockColumn = DataTableColumn<StockStatusItem>

export const EMPTY_CELL = '—'

const BASE_COLUMNS: StockColumn[] = [
  { key: 'product', header: 'Produit', render: (item) => item.productName },
  { key: 'category', header: 'Catégorie', render: (item) => item.category ?? EMPTY_CELL },
  { key: 'stock', header: 'Stock', render: (item) => formatStockQuantity(item.quantity, item.unit) },
  { key: 'threshold', header: 'Seuil', render: (item) => item.minThreshold },
]

const lotColumns = (isNew: boolean): StockColumn[] => [
  { key: 'lot', header: 'N° de lot', isNew, render: (item) => item.lotNumber ?? EMPTY_CELL },
  {
    key: 'expiry',
    header: 'Péremption',
    isNew,
    render: (item) => (item.expiryDate ? formatIsoDate(item.expiryDate) : EMPTY_CELL),
  },
]

export const buildStockColumns = ({ enabled, highlighted }: StockAdaptationView): StockColumn[] => [
  ...BASE_COLUMNS,
  ...(enabled ? lotColumns(highlighted) : []),
  {
    key: 'status',
    header: 'Statut',
    render: (item) => <StockStatusPill item={item} withExpiry={enabled} />,
  },
]

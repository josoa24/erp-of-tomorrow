import type { ReactNode } from 'react'
import { DataTable } from '../../../components/module/DataTable'
import type { StockAdaptationView } from '../services/stockAdaptation'
import { buildStockRowActions, type StockRowHandlers } from '../services/stockRowActions'
import type { StockStatusItem } from '../types/stock'
import { buildStockColumns } from './stockColumns'

interface StockTableProps {
  items: StockStatusItem[]
  loading: boolean
  adaptation: StockAdaptationView
  handlers: StockRowHandlers
  empty: ReactNode
}

export const StockTable = ({ items, loading, adaptation, handlers, empty }: StockTableProps) => (
  <DataTable
    caption="Niveaux de stock par produit"
    columns={buildStockColumns(adaptation)}
    rows={items}
    getRowKey={(item) => item.productId}
    getRowLabel={(item) => item.productName}
    rowActions={(item) => buildStockRowActions(item, handlers, adaptation.enabled)}
    loading={loading}
    empty={empty}
  />
)

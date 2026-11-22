import type { ReactNode } from 'react'
import { DataTable } from '../../../components/module/DataTable'
import { buildSaleRowActions, type SaleRowHandlers } from '../services/salesRowActions'
import type { Sale } from '../types/sale'
import { buildSalesColumns } from './salesColumns'

interface SalesTableProps {
  sales: Sale[]
  loading: boolean
  adapted: boolean
  highlighted: boolean
  handlers: SaleRowHandlers
  empty: ReactNode
}

export const SalesTable = ({ sales, loading, adapted, highlighted, handlers, empty }: SalesTableProps) => (
  <DataTable
    caption="Ventes"
    columns={buildSalesColumns(adapted, highlighted)}
    rows={sales}
    getRowKey={(sale) => sale.id}
    getRowLabel={(sale) => sale.number}
    rowActions={(sale) => buildSaleRowActions(sale, handlers)}
    loading={loading}
    empty={empty}
  />
)

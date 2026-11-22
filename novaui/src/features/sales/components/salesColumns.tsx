import type { DataTableColumn } from '../../../components/module/types'
import { formatSaleDateTime, formatDueDate } from '../services/saleDateFormat'
import { paymentTermsLabel } from '../services/paymentTerms'
import { formatAmount } from '../services/salesFormat'
import type { Sale } from '../types/sale'
import { PaymentStatusPill } from './PaymentStatusPill'

type SaleColumn = DataTableColumn<Sale>

export const EMPTY_CELL = '—'

const BASE_COLUMNS: SaleColumn[] = [
  { key: 'number', header: 'Vente', render: (sale) => sale.number },
  {
    key: 'customer',
    header: 'Client',
    render: (sale) => (sale.toDeliver ? `${sale.customerName} · À livrer` : sale.customerName),
  },
  { key: 'date', header: 'Date', render: (sale) => formatSaleDateTime(sale.validatedAt ?? sale.createdAt) },
  { key: 'total', header: 'Total', align: 'end', render: (sale) => formatAmount(sale.totalAmount) },
]

const paymentColumns = (isNew: boolean): SaleColumn[] => [
  { key: 'terms', header: 'Paiement', isNew, render: (sale) => paymentTermsLabel(sale) ?? EMPTY_CELL },
  { key: 'due', header: 'Échéance', isNew, render: (sale) => (sale.dueDate ? formatDueDate(sale.dueDate) : EMPTY_CELL) },
]

export const buildSalesColumns = (adapted: boolean, highlighted: boolean): SaleColumn[] => [
  ...BASE_COLUMNS,
  ...(adapted ? paymentColumns(highlighted) : []),
  { key: 'status', header: 'Statut', render: (sale) => <PaymentStatusPill status={sale.paymentStatus} /> },
]

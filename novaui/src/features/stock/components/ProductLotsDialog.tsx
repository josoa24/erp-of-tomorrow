import { Layers } from 'lucide-react'
import { DataTable } from '../../../components/module/DataTable'
import type { DataTableColumn } from '../../../components/module/types'
import { Dialog } from '../../../components/ui/dialog'
import { EmptyState } from '../../../components/ui/empty-state'
import { useProductLots } from '../hooks/useProductLots'
import { formatInstantDate, formatIsoDate } from '../services/stockDates'
import { formatStockQuantity } from '../services/stockQuantity'
import type { StockLot, StockStatusItem } from '../types/stock'

interface ProductLotsDialogProps {
  item: StockStatusItem
  onClose: () => void
}

const lotColumns = (unit: string): DataTableColumn<StockLot>[] => [
  { key: 'lot', header: 'N° de lot', render: (lot) => lot.lotNumber },
  { key: 'expiry', header: 'Péremption', render: (lot) => formatIsoDate(lot.expiryDate) },
  { key: 'remaining', header: 'Restant', render: (lot) => formatStockQuantity(lot.remaining, unit) },
  { key: 'quantity', header: 'Reçu', render: (lot) => formatStockQuantity(lot.quantity, unit) },
  { key: 'received', header: 'Reçu le', render: (lot) => formatInstantDate(lot.receivedAt) },
]

export const ProductLotsDialog = ({ item, onClose }: ProductLotsDialogProps) => {
  const { lots, loading } = useProductLots(item.productId)

  return (
    <Dialog open onClose={onClose} size="l" title="Lots du produit" description={item.productName}>
      <DataTable
        caption={`Lots de ${item.productName}`}
        columns={lotColumns(item.unit)}
        rows={lots}
        getRowKey={(lot) => lot.lotNumber}
        getRowLabel={(lot) => lot.lotNumber}
        loading={loading}
        className="rounded-lg border border-line-subtle"
        empty={
          <EmptyState
            icon={Layers}
            title="Aucun lot"
            description="Aucune entrée avec n° de lot n’a été enregistrée pour ce produit."
          />
        }
      />
    </Dialog>
  )
}

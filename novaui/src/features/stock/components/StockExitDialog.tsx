import { useId } from 'react'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { useMovementForm } from '../hooks/useMovementForm'
import { useRecordMovement } from '../hooks/useRecordMovement'
import { todayIsoDate } from '../services/stockDates'
import { formatStockQuantity } from '../services/stockQuantity'
import type { StockStatusItem } from '../types/stock'
import { MovementFields } from './MovementFields'

export const FEFO_NOTE = 'Les lots les plus proches de la péremption sortent en premier.'

interface StockExitDialogProps {
  item: StockStatusItem
  withLots: boolean
  onClose: () => void
}

export const StockExitDialog = ({ item, withLots, onClose }: StockExitDialogProps) => {
  const rules = { type: 'OUT' as const, withLot: false, today: todayIsoDate(), available: item.quantity }
  const form = useMovementForm(rules, item.productId)
  const { record, recording } = useRecordMovement(onClose)
  const formId = useId()

  return (
    <Dialog
      open
      onClose={onClose}
      title="Sortie de stock"
      description={withLots ? `${item.productName} · ${FEFO_NOTE}` : item.productName}
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>
            Annuler
          </Button>
          <Button type="submit" form={formId} loading={recording}>
            Enregistrer la sortie
          </Button>
        </>
      }
    >
      <form
        id={formId}
        noValidate
        className="flex flex-col gap-5"
        onSubmit={form.submit((productId, movement) => record({ productId, movement }))}
      >
        <MovementFields
          quantityLabel="Quantité sortie"
          quantityHint={`Stock disponible : ${formatStockQuantity(item.quantity, item.unit)}`}
          reasonPlaceholder="Ex. casse, usage interne"
          values={form.values}
          errors={form.errors}
          onChange={form.setField}
        />
      </form>
    </Dialog>
  )
}

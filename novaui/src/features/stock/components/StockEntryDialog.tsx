import { useId } from 'react'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { useMovementForm } from '../hooks/useMovementForm'
import { useRecordMovement } from '../hooks/useRecordMovement'
import type { StockAdaptationView } from '../services/stockAdaptation'
import { todayIsoDate } from '../services/stockDates'
import { formatStockQuantity } from '../services/stockQuantity'
import type { StockStatusItem } from '../types/stock'
import { LotFields } from './LotFields'
import { MovementFields } from './MovementFields'
import { ProductPicker } from './ProductPicker'

interface StockEntryDialogProps {
  item: StockStatusItem | null
  adaptation: StockAdaptationView
  onClose: () => void
}

export const StockEntryDialog = ({ item, adaptation, onClose }: StockEntryDialogProps) => {
  const today = todayIsoDate()
  const form = useMovementForm({ type: 'IN', withLot: adaptation.enabled, today }, item?.productId ?? null)
  const { record, recording } = useRecordMovement(onClose)
  const formId = useId()

  return (
    <Dialog
      open
      onClose={onClose}
      title="Entrée de stock"
      description={item ? item.productName : 'Enregistrez la marchandise reçue.'}
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>
            Annuler
          </Button>
          <Button type="submit" form={formId} loading={recording}>
            Enregistrer l’entrée
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
        {!item && (
          <ProductPicker
            value={form.values.productId}
            error={form.errors.productId}
            onChange={(productId) => form.setField('productId', productId)}
          />
        )}
        <MovementFields
          quantityLabel="Quantité reçue"
          quantityHint={item ? `Stock actuel : ${formatStockQuantity(item.quantity, item.unit)}` : undefined}
          reasonPlaceholder="Ex. livraison du fournisseur"
          values={form.values}
          errors={form.errors}
          onChange={form.setField}
        />
        {adaptation.enabled && (
          <LotFields
            values={form.values}
            errors={form.errors}
            today={today}
            highlighted={adaptation.highlighted}
            onChange={form.setField}
          />
        )}
      </form>
    </Dialog>
  )
}

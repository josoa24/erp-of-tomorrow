import { Trash2 } from 'lucide-react'
import { IconButton } from '../../../components/ui/icon-button'
import { Input } from '../../../components/ui/input'
import { exceedsStock, lineTotal, type DraftLine } from '../services/saleDraft'
import { formatAmount } from '../services/salesFormat'

interface DraftLineRowProps {
  line: DraftLine
  onQuantity: (productId: number, quantity: number) => void
  onRemove: (productId: number) => void
}

export const DraftLineRow = ({ line, onQuantity, onRemove }: DraftLineRowProps) => (
  <li className="flex flex-wrap items-end gap-3">
    <div className="min-w-0 flex-1 basis-40">
      <p className="text-body-s text-ink-primary">{line.productName}</p>
      <p className="text-micro text-ink-tertiary">{formatAmount(line.unitPrice)} l’unité</p>
    </div>
    <Input
      label={`Quantité de ${line.productName}`}
      hideLabel
      type="number"
      inputMode="numeric"
      min={1}
      value={Number.isNaN(line.quantity) ? '' : line.quantity}
      invalid={exceedsStock(line)}
      helper={exceedsStock(line) ? `Stock disponible : ${line.available}` : undefined}
      containerClassName="w-28"
      onChange={(event) => onQuantity(line.productId, event.target.valueAsNumber)}
    />
    <p className="w-28 pb-3 text-right text-body-s text-ink-primary">{formatAmount(lineTotal(line))}</p>
    <IconButton aria-label={`Retirer ${line.productName}`} onClick={() => onRemove(line.productId)}>
      <Trash2 aria-hidden="true" className="size-[18px]" />
    </IconButton>
  </li>
)

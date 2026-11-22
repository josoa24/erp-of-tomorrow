import { useId } from 'react'
import { Button } from '../../../components/ui/button'
import { Checkbox } from '../../../components/ui/checkbox'
import { Dialog } from '../../../components/ui/dialog'
import { useSaleDraftState } from '../hooks/useSaleDraftState'
import { useSubmitSale } from '../hooks/useSaleCommands'
import { canSaveDraft, canValidateDraft, draftTotal, EMPTY_DRAFT } from '../services/saleDraft'
import { draftFromSale, toSaleRequest } from '../services/saleDraftRequest'
import { formatAmount } from '../services/salesFormat'
import type { Sale } from '../types/sale'
import { CustomerPicker } from './CustomerPicker'
import { PaymentTermsFields } from './PaymentTermsFields'
import { ProductLinesEditor } from './ProductLinesEditor'

interface SaleFormDialogProps {
  sale: Sale | null
  adapted: boolean
  highlighted: boolean
  onClose: () => void
}

export const SaleFormDialog = ({ sale, adapted, highlighted, onClose }: SaleFormDialogProps) => {
  const state = useSaleDraftState(sale ? draftFromSale(sale) : EMPTY_DRAFT)
  const { draft } = state
  const { mutate, isPending } = useSubmitSale()
  const formId = useId()
  const submit = (validate: boolean) =>
    mutate({ saleId: sale?.id ?? null, request: toSaleRequest(draft, adapted), validate }, { onSuccess: onClose })

  return (
    <Dialog
      open
      onClose={onClose}
      size="l"
      title={sale ? `Modifier ${sale.number}` : 'Nouvelle vente'}
      description={`Total : ${formatAmount(draftTotal(draft))}`}
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>Annuler</Button>
          <Button variant="secondary" disabled={!canSaveDraft(draft) || isPending} onClick={() => submit(false)}>
            Enregistrer le brouillon
          </Button>
          <Button type="submit" form={formId} disabled={!canValidateDraft(draft)} loading={isPending}>
            Valider la vente
          </Button>
        </>
      }
    >
      <form id={formId} noValidate className="flex flex-col gap-5" onSubmit={(event) => { event.preventDefault(); submit(true) }}>
        <CustomerPicker value={draft.customer} onChange={state.setCustomer} />
        <ProductLinesEditor lines={draft.lines} onAdd={state.addProduct} onQuantity={state.setQuantity} onRemove={state.removeLine} />
        <Checkbox label="À livrer" checked={draft.toDeliver} onChange={(event) => state.setToDeliver(event.target.checked)} />
        {adapted && (
          <PaymentTermsFields
            method={draft.paymentMethod}
            creditDays={draft.creditDays}
            highlighted={highlighted}
            onMethod={state.setMethod}
            onCreditDays={state.setCreditDays}
          />
        )}
      </form>
    </Dialog>
  )
}

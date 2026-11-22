import { useId, useState } from 'react'
import { Button } from '../../../components/ui/button'
import { ChoiceChip } from '../../../components/ui/choice-chip'
import { Dialog } from '../../../components/ui/dialog'
import { Input } from '../../../components/ui/input'
import { useRecordPayment } from '../hooks/useSaleCommands'
import { defaultCollectAmount, defaultCollectMethod, isCollectAmountValid, parseAmount } from '../services/collectForm'
import { COLLECT_METHODS, PAYMENT_METHOD_LABELS } from '../services/paymentTerms'
import { remainingAmount } from '../services/saleActions'
import { formatAmount } from '../services/salesFormat'
import type { Sale } from '../types/sale'

interface CollectDialogProps {
  sale: Sale
  onClose: () => void
}

export const CollectDialog = ({ sale, onClose }: CollectDialogProps) => {
  const [amountText, setAmountText] = useState(defaultCollectAmount(sale))
  const [method, setMethod] = useState(defaultCollectMethod(sale))
  const { mutate, isPending } = useRecordPayment()
  const formId = useId()
  const remaining = remainingAmount(sale)
  const amount = parseAmount(amountText)
  const valid = isCollectAmountValid(amount, remaining)

  return (
    <Dialog
      open
      onClose={onClose}
      title={`Encaisser ${sale.number}`}
      description={`Reste à encaisser : ${formatAmount(remaining)}`}
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>Annuler</Button>
          <Button type="submit" form={formId} disabled={!valid} loading={isPending}>Enregistrer l’encaissement</Button>
        </>
      }
    >
      <form
        id={formId}
        noValidate
        className="flex flex-col gap-5"
        onSubmit={(event) => { event.preventDefault(); mutate({ sale, request: { amount, method } }, { onSuccess: onClose }) }}
      >
        <Input
          label="Montant encaissé"
          inputMode="decimal"
          value={amountText}
          invalid={amountText !== '' && !valid}
          helper={amountText !== '' && !valid ? `Entre 0 et ${formatAmount(remaining)}` : undefined}
          onChange={(event) => setAmountText(event.target.value)}
        />
        <div className="flex flex-wrap gap-2" role="group" aria-label="Moyen de paiement">
          {COLLECT_METHODS.map((option) => (
            <ChoiceChip key={option} label={PAYMENT_METHOD_LABELS[option]} selected={method === option} onClick={() => setMethod(option)} />
          ))}
        </div>
      </form>
    </Dialog>
  )
}

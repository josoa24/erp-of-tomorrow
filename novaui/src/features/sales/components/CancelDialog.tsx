import { useId, useState } from 'react'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { Textarea } from '../../../components/ui/textarea'
import { useCancelSale } from '../hooks/useSaleCommands'
import type { Sale } from '../types/sale'

interface CancelDialogProps {
  sale: Sale
  onClose: () => void
}

export const CancelDialog = ({ sale, onClose }: CancelDialogProps) => {
  const [reason, setReason] = useState('')
  const { mutate, isPending } = useCancelSale()
  const formId = useId()

  return (
    <Dialog
      open
      onClose={onClose}
      title={`Annuler ${sale.number}`}
      description="Les produits retournent en stock. Une vente déjà encaissée ne peut pas être annulée."
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>Garder la vente</Button>
          <Button type="submit" form={formId} variant="danger" disabled={reason.trim() === ''} loading={isPending}>
            Annuler la vente
          </Button>
        </>
      }
    >
      <form
        id={formId}
        noValidate
        onSubmit={(event) => { event.preventDefault(); mutate({ sale, reason: reason.trim() }, { onSuccess: onClose }) }}
      >
        <Textarea label="Motif de l’annulation" maxLength={500} value={reason} onChange={(event) => setReason(event.target.value)} />
      </form>
    </Dialog>
  )
}

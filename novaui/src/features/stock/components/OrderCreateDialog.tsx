import { Plus } from 'lucide-react'
import { useId, useMemo } from 'react'
import { Combobox } from '@/components/ui/combobox'
import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { useCreateOrder } from '../hooks/useOrderCommands'
import { useOrderForm } from '../hooks/useOrderForm'
import { formatOrderAmount } from '../services/orderFormat'
import type { Supplier } from '../types/purchaseOrder'
import { OrderLineRow } from './OrderLineRow'

interface OrderCreateDialogProps {
  suppliers: Supplier[]
  onClose: () => void
}

export const OrderCreateDialog = ({ suppliers, onClose }: OrderCreateDialogProps) => {
  const form = useOrderForm()
  const { mutate, isPending } = useCreateOrder(onClose)
  const formId = useId()
  const options = useMemo(
    () => suppliers.map((supplier) => ({ value: String(supplier.id), label: supplier.name, description: supplier.city ?? undefined })),
    [suppliers],
  )
  const selected = options.find((option) => option.value === String(form.supplierId)) ?? null

  return (
    <Dialog
      open
      onClose={onClose}
      size="l"
      title="Nouvelle commande fournisseur"
      description="La commande est enregistrée en brouillon. Une fois validée, elle ne change plus."
      footer={
        <>
          <p className="mr-auto self-center text-label text-ink-primary">Total : {formatOrderAmount(form.total)}</p>
          <Button variant="secondary" onClick={onClose} disabled={isPending}>Annuler</Button>
          <Button type="submit" form={formId} loading={isPending} disabled={!form.valid}>Enregistrer le brouillon</Button>
        </>
      }
    >
      <form
        id={formId}
        className="flex flex-col gap-4"
        onSubmit={(event) => {
          event.preventDefault()
          const request = form.request()
          if (request && form.valid) mutate(request)
        }}
      >
        <Combobox
          label="Fournisseur"
          placeholder="Choisir un fournisseur"
          options={options}
          value={selected}
          onChange={(option) => form.setSupplierId(option ? Number(option.value) : null)}
        />
        {form.lines.map((line) => (
          <OrderLineRow key={line.key} line={line} removable={form.lines.length > 1} onChange={form.change} onRemove={form.remove} />
        ))}
        <Button type="button" variant="secondary" leadingIcon={<Plus aria-hidden="true" />} onClick={form.add} className="self-start">
          Ajouter une ligne
        </Button>
      </form>
    </Dialog>
  )
}

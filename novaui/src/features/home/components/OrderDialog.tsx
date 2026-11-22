import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { useDraftOrder } from '../hooks/useDraftOrder'
import type { Replenishment } from '../types/home'

interface OrderDialogProps {
  item: Replenishment
  onClose: () => void
}

export const OrderDialog = ({ item, onClose }: OrderDialogProps) => {
  const { order, ordering } = useDraftOrder(item, onClose)

  return (
    <Dialog
      open
      onClose={onClose}
      title="Commander ce produit"
      description={`Une commande fournisseur brouillon de ${item.recommendedQuantity} unités de ${item.productName} sera enregistrée. Vous pourrez la valider dans le Stock.`}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={ordering}>Annuler</Button>
          <Button onClick={order} loading={ordering}>Créer la commande</Button>
        </>
      }
    />
  )
}

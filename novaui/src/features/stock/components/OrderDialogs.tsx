import { ConfirmDialog } from '../../modules/components/ConfirmDialog'
import { useReceiveOrder, useValidateOrder } from '../hooks/useOrderCommands'
import type { OrderDialog } from '../hooks/useOrderDialogs'
import { formatOrderAmount, orderNumber } from '../services/orderFormat'
import type { Supplier } from '../types/purchaseOrder'
import { OrderCreateDialog } from './OrderCreateDialog'
import { OrderDetailDialog } from './OrderDetailDialog'

interface OrderDialogsProps {
  dialog: OrderDialog | null
  suppliers: Supplier[]
  onClose: () => void
}

export const OrderDialogs = ({ dialog, suppliers, onClose }: OrderDialogsProps) => {
  const validate = useValidateOrder(onClose)
  const receive = useReceiveOrder(onClose)
  if (dialog === null) return null
  if (dialog.type === 'create') return <OrderCreateDialog suppliers={suppliers} onClose={onClose} />
  if (dialog.type === 'view') return <OrderDetailDialog order={dialog.order} suppliers={suppliers} onClose={onClose} />
  const { order } = dialog
  const isValidation = dialog.type === 'validate'
  return (
    <ConfirmDialog
      title={isValidation ? `Valider la commande ${orderNumber(order)} ?` : `Réceptionner la commande ${orderNumber(order)} ?`}
      description={
        isValidation
          ? `Montant ${formatOrderAmount(order.totalAmount)}. Une commande validée ne peut plus être modifiée.`
          : 'Une entrée de stock est enregistrée pour chaque ligne. Cette réception est définitive.'
      }
      confirmLabel={isValidation ? 'Valider' : 'Réceptionner'}
      loading={validate.isPending || receive.isPending}
      onConfirm={() => (isValidation ? validate.mutate(order.id) : receive.mutate(order.id))}
      onClose={onClose}
    />
  )
}

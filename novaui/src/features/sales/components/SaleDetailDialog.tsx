import { Button } from '../../../components/ui/button'
import { Dialog } from '../../../components/ui/dialog'
import { useSalePayments } from '../hooks/useSalePayments'
import { formatSaleDateTime } from '../services/saleDateFormat'
import { PAYMENT_METHOD_LABELS } from '../services/paymentTerms'
import { formatAmount } from '../services/salesFormat'
import type { Sale } from '../types/sale'
import { PaymentStatusPill } from './PaymentStatusPill'

interface SaleDetailDialogProps {
  sale: Sale
  onClose: () => void
}

export const SaleDetailDialog = ({ sale, onClose }: SaleDetailDialogProps) => {
  const { payments } = useSalePayments(sale.id, sale.paymentStatus !== 'DRAFT')

  return (
    <Dialog
      open
      onClose={onClose}
      title={`Vente ${sale.number}`}
      description={`${sale.customerName} · ${formatAmount(sale.totalAmount)}`}
      footer={<Button variant="secondary" onClick={onClose}>Fermer</Button>}
    >
      <div className="flex flex-col gap-4">
        <PaymentStatusPill status={sale.paymentStatus} />
        <ul className="flex flex-col gap-1 text-body-s text-ink-primary">
          {sale.lines.map((line) => (
            <li key={line.productId} className="flex justify-between gap-3">
              <span>{line.quantity} × {line.productName}</span>
              <span>{formatAmount(line.lineTotal)}</span>
            </li>
          ))}
        </ul>
        {payments.length > 0 && (
          <ul aria-label="Encaissements" className="flex flex-col gap-1 text-body-s text-ink-secondary">
            {payments.map((payment) => (
              <li key={payment.id} className="flex justify-between gap-3">
                <span>{PAYMENT_METHOD_LABELS[payment.method]} · {formatSaleDateTime(payment.paidAt)}</span>
                <span>{formatAmount(payment.amount)}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </Dialog>
  )
}

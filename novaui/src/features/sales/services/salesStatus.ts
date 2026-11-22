import type { StatusTone } from '../../../components/ui/status-pill'
import type { PaymentStatus } from '../types/sale'

export const PAYMENT_STATUS_VIEWS: Readonly<Record<PaymentStatus, { label: string; tone: StatusTone }>> = {
  DRAFT: { label: 'Brouillon', tone: 'neutral' },
  TO_COLLECT: { label: 'À encaisser', tone: 'info' },
  OVERDUE: { label: 'En retard', tone: 'critical' },
  PAID: { label: 'Payée', tone: 'success' },
  CANCELLED: { label: 'Annulée', tone: 'neutral' },
}

import { StatusPill } from '../../../components/ui/status-pill'
import { PAYMENT_STATUS_VIEWS } from '../services/salesStatus'
import type { PaymentStatus } from '../types/sale'

export const PaymentStatusPill = ({ status }: { status: PaymentStatus }) => {
  const { label, tone } = PAYMENT_STATUS_VIEWS[status]
  return <StatusPill label={label} tone={tone} />
}

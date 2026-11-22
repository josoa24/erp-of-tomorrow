import { StatusPill, type StatusTone } from '../../../components/ui/status-pill'
import type { ContactStatus } from '../types/contact'

const STATUS_DISPLAY: Record<ContactStatus, { label: string; tone: StatusTone }> = {
  ACTIVE: { label: 'Actif', tone: 'success' },
  FOLLOW_UP: { label: 'À relancer', tone: 'warning' },
}

export const ContactStatusPill = ({ status }: { status: ContactStatus }) => <StatusPill {...STATUS_DISPLAY[status]} />

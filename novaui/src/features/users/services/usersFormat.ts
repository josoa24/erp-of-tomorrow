import { SALES_TIME_ZONE } from '../../sales/services/salesDates'
import type { ManagedUser } from '../types/user'

export const NEVER_CONNECTED = 'Jamais connecté'
export const UNNAMED_USER = 'Sans nom'

const dateTime = new Intl.DateTimeFormat('fr-FR', {
  timeZone: SALES_TIME_ZONE,
  day: 'numeric',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
})
const date = new Intl.DateTimeFormat('fr-FR', { timeZone: SALES_TIME_ZONE, day: 'numeric', month: 'short', year: 'numeric' })

export const formatLastLogin = (iso: string | null): string => (iso ? dateTime.format(new Date(iso)) : NEVER_CONNECTED)

export const formatCreatedAt = (iso: string): string => date.format(new Date(iso))

export const userDisplayName = (user: Pick<ManagedUser, 'fullName'>): string => user.fullName?.trim() || UNNAMED_USER

export const isSameAccount = (user: Pick<ManagedUser, 'email'>, currentEmail: string | undefined): boolean =>
  currentEmail !== undefined && user.email.toLowerCase() === currentEmail.toLowerCase()

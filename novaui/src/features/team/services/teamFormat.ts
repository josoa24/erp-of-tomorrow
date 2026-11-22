import { CURRENCY_LABEL } from '../../../constants/appInfo'
import { toUtcTime } from '../../../utils/isoDate'
import { CONTRACT_LABELS } from '../constants/contracts'
import type { ContractType } from '../types/employee'

export const EMPTY_VALUE = '—'

const amountFormatter = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 })
const monthYearFormatter = new Intl.DateTimeFormat('fr-FR', { month: 'short', year: 'numeric', timeZone: 'UTC' })
const dayMonthFormatter = new Intl.DateTimeFormat('fr-FR', { day: 'numeric', month: 'short', timeZone: 'UTC' })

export const formatAmount = (value: number): string => `${amountFormatter.format(value)} ${CURRENCY_LABEL}`

export const formatArrival = (isoDate: string): string => monthYearFormatter.format(toUtcTime(isoDate))

export const formatContract = (contract: ContractType | null): string =>
  contract ? CONTRACT_LABELS[contract] : EMPTY_VALUE

const formatDay = (isoDate: string): string => dayMonthFormatter.format(toUtcTime(isoDate))

export const formatPeriod = (startDate: string, endDate: string): string =>
  startDate === endDate ? `le ${formatDay(startDate)}` : `du ${formatDay(startDate)} au ${formatDay(endDate)}`

export const fullName = ({ firstName, lastName }: { firstName: string; lastName: string }): string =>
  `${firstName} ${lastName}`.trim()

export const formatMonthAdvance = (amount: number, pending: boolean): string => {
  if (amount <= 0) return EMPTY_VALUE
  return pending ? `${formatAmount(amount)} · à valider` : formatAmount(amount)
}

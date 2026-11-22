import { businessDaysBetween } from './businessDay'

export const NO_EXCHANGE_LABEL = 'Aucun échange'

const nearby = new Intl.RelativeTimeFormat('fr-FR', { numeric: 'auto' })
const counted = new Intl.RelativeTimeFormat('fr-FR', { numeric: 'always' })

const DAYS_PER_MONTH = 30
const DAYS_PER_YEAR = 365
const MONTHS_FROM_DAYS = 60

export const formatLastExchange = (isoInstant: string | null, now: Date = new Date()): string => {
  if (!isoInstant) return NO_EXCHANGE_LABEL
  const days = Math.max(0, businessDaysBetween(new Date(isoInstant), now))
  if (days <= 1) return nearby.format(-days, 'day')
  if (days < MONTHS_FROM_DAYS) return counted.format(-days, 'day')
  if (days < DAYS_PER_YEAR) return counted.format(-Math.floor(days / DAYS_PER_MONTH), 'month')
  return counted.format(-Math.floor(days / DAYS_PER_YEAR), 'year')
}

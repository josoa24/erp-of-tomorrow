import type { DateRange } from '../../../types/dashboard'

export const SALES_TIME_ZONE = 'Indian/Antananarivo'

const isoDayFormatter = new Intl.DateTimeFormat('en-CA', {
  timeZone: SALES_TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
})

const monthFormatter = (timeZone: string) => new Intl.DateTimeFormat('fr-FR', { timeZone, month: 'long' })

const capitalize = (text: string): string => text.charAt(0).toLocaleUpperCase('fr-FR') + text.slice(1)

export const todayIsoDate = (now = new Date()): string => isoDayFormatter.format(now)

export const monthToDate = (now = new Date()): DateRange => {
  const to = todayIsoDate(now)
  return { from: `${to.slice(0, 8)}01`, to }
}

export const currentMonthLabel = (now = new Date()): string =>
  capitalize(monthFormatter(SALES_TIME_ZONE).format(now))

export const previousMonthLabel = (now = new Date()): string => {
  const [year, month] = todayIsoDate(now).split('-').map(Number)
  return monthFormatter('UTC').format(new Date(Date.UTC(year, month - 2, 15)))
}

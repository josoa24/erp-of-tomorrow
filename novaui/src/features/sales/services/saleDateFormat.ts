import { SALES_TIME_ZONE } from './salesDates'

const dayFormatter = new Intl.DateTimeFormat('fr-FR', { timeZone: SALES_TIME_ZONE, day: 'numeric', month: 'short' })
const timeFormatter = new Intl.DateTimeFormat('fr-FR', {
  timeZone: SALES_TIME_ZONE,
  hour: '2-digit',
  minute: '2-digit',
})
const dueDateFormatter = new Intl.DateTimeFormat('fr-FR', {
  timeZone: 'UTC',
  day: 'numeric',
  month: 'short',
  year: 'numeric',
})

export const formatSaleDateTime = (iso: string): string => {
  const date = new Date(iso)
  return `${dayFormatter.format(date)} · ${timeFormatter.format(date)}`
}

export const formatDueDate = (isoDate: string): string => dueDateFormatter.format(new Date(`${isoDate}T00:00:00Z`))

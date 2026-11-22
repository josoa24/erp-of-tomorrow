export const STOCK_TIME_ZONE = 'Indian/Antananarivo'

const isoDayFormatter = new Intl.DateTimeFormat('en-CA', {
  timeZone: STOCK_TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
})

const displayDayFormatter = new Intl.DateTimeFormat('fr-FR', {
  timeZone: STOCK_TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
})

export const todayIsoDate = (now = new Date()): string => isoDayFormatter.format(now)

export const formatIsoDate = (isoDate: string): string => {
  const [year, month, day] = isoDate.split('-')
  return `${day}/${month}/${year}`
}

export const formatInstantDate = (instant: string): string => displayDayFormatter.format(new Date(instant))

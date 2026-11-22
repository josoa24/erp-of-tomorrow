import { BUSINESS_TIME_ZONE } from './businessDay'

const dateFormatter = new Intl.DateTimeFormat('fr-FR', {
  day: 'numeric',
  month: 'short',
  year: 'numeric',
  timeZone: BUSINESS_TIME_ZONE,
})

export const formatTimelineDate = (isoInstant: string): string => dateFormatter.format(new Date(isoInstant))

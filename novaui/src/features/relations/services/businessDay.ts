import { DAY_MS, toUtcTime } from '../../../utils/isoDate'

export const BUSINESS_TIME_ZONE = 'Indian/Antananarivo'

const isoDateFormatter = new Intl.DateTimeFormat('en-CA', {
  timeZone: BUSINESS_TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
})

export const toBusinessIsoDate = (date: Date): string => isoDateFormatter.format(date)

export const businessDaysBetween = (from: Date, to: Date): number =>
  Math.round((toUtcTime(toBusinessIsoDate(to)) - toUtcTime(toBusinessIsoDate(from))) / DAY_MS)

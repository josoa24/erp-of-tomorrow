import type { DateRange, PeriodKey } from '../types/dashboard'
import { DAY_MS, fromUtcTime, toLocalIsoDate, toUtcTime } from './isoDate'

export const getPeriodRange = (period: PeriodKey, today = new Date()): DateRange => {
  const to = toLocalIsoDate(today)
  if (period === 'today') return { from: to, to }
  if (period === 'week') return { from: fromUtcTime(toUtcTime(to) - 6 * DAY_MS), to }
  return { from: `${to.slice(0, 8)}01`, to }
}

export const getPreviousRange = ({ from, to }: DateRange): DateRange => {
  const start = toUtcTime(from)
  const length = toUtcTime(to) - start + DAY_MS
  return { from: fromUtcTime(start - length), to: fromUtcTime(start - DAY_MS) }
}

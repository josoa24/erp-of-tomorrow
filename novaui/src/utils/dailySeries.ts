import type { DateRange } from '../types/dashboard'
import type { DailyRevenue } from '../types/sales'
import { DAY_MS, fromUtcTime, toUtcTime } from './isoDate'

export const buildDailySeries = (daily: DailyRevenue[], { from, to }: DateRange): number[] => {
  const revenueByDate = new Map(daily.map((day) => [day.date, day.revenue]))
  const series: number[] = []
  for (let time = toUtcTime(from); time <= toUtcTime(to); time += DAY_MS) {
    series.push(revenueByDate.get(fromUtcTime(time)) ?? 0)
  }
  return series
}

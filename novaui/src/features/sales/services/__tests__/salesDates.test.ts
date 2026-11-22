import { formatDueDate, formatSaleDateTime } from '../saleDateFormat'
import { currentMonthLabel, monthToDate, previousMonthLabel, todayIsoDate } from '../salesDates'

const normalize = (text: string): string => text.replace(/\s/g, ' ')

describe('sales dates', () => {
  it('shouldUseAntananarivoDay_whenUtcIsStillYesterday', () => {
    expect(todayIsoDate(new Date('2026-11-30T22:30:00Z'))).toBe('2026-12-01')
  })

  it('shouldStartRangeOnFirstDayOfMonth', () => {
    expect(monthToDate(new Date('2026-11-24T08:00:00Z'))).toEqual({ from: '2026-11-01', to: '2026-11-24' })
  })

  it('shouldNameCurrentAndPreviousMonth', () => {
    const now = new Date('2026-11-24T08:00:00Z')

    expect(currentMonthLabel(now)).toBe('Novembre')
    expect(previousMonthLabel(now)).toBe('octobre')
  })

  it('shouldNameDecember_whenCurrentMonthIsJanuary', () => {
    expect(previousMonthLabel(new Date('2027-01-05T08:00:00Z'))).toBe('décembre')
  })

  it('shouldFormatSaleDateInLocalTime', () => {
    expect(normalize(formatSaleDateTime('2026-11-24T07:42:00Z'))).toBe('24 nov. · 10:42')
  })

  it('shouldFormatDueDate', () => {
    expect(normalize(formatDueDate('2026-12-24'))).toBe('24 déc. 2026')
  })
})

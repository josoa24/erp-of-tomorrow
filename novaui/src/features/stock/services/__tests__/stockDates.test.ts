import { formatInstantDate, formatIsoDate, todayIsoDate } from '../stockDates'

describe('stockDates', () => {
  it('shouldFormatIsoDate_asDayMonthYear', () => {
    expect(formatIsoDate('2026-12-17')).toBe('17/12/2026')
  })

  it('shouldComputeToday_inAntananarivo', () => {
    expect(todayIsoDate(new Date('2026-11-21T22:30:00Z'))).toBe('2026-11-22')
  })

  it('shouldFormatInstant_inAntananarivo', () => {
    expect(formatInstantDate('2026-11-21T22:30:00Z')).toBe('22/11/2026')
  })
})

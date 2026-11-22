import { DAY_MS, fromUtcTime, toLocalIsoDate, toUtcTime } from '../isoDate'

describe('isoDate', () => {
  it('shouldFormatLocalDate_withPadding', () => {
    expect(toLocalIsoDate(new Date(2026, 0, 5, 23, 59))).toBe('2026-01-05')
  })

  it('shouldRoundTripThroughUtcTime', () => {
    expect(fromUtcTime(toUtcTime('2026-12-31') + DAY_MS)).toBe('2027-01-01')
  })
})

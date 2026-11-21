import { buildDailySeries } from '../dailySeries'

describe('buildDailySeries', () => {
  it('shouldFillMissingDaysWithZero', () => {
    const daily = [
      { date: '2026-11-02', salesCount: 1, revenue: 500 },
      { date: '2026-11-04', salesCount: 2, revenue: 900 },
    ]

    const series = buildDailySeries(daily, { from: '2026-11-01', to: '2026-11-04' })

    expect(series).toEqual([0, 500, 0, 900])
  })

  it('shouldReturnZeros_whenNoSales', () => {
    expect(buildDailySeries([], { from: '2026-11-01', to: '2026-11-02' })).toEqual([0, 0])
  })
})

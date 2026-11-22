import { getPeriodRange, getPreviousRange } from '../period'

const today = new Date(2026, 10, 21, 15, 30)

describe('getPeriodRange', () => {
  it('shouldReturnSingleDay_whenToday', () => {
    expect(getPeriodRange('today', today)).toEqual({ from: '2026-11-21', to: '2026-11-21' })
  })

  it('shouldReturnSevenDays_whenWeek', () => {
    expect(getPeriodRange('week', today)).toEqual({ from: '2026-11-15', to: '2026-11-21' })
  })

  it('shouldStartOnFirstDay_whenMonth', () => {
    expect(getPeriodRange('month', today)).toEqual({ from: '2026-11-01', to: '2026-11-21' })
  })
})

describe('getPreviousRange', () => {
  it('shouldReturnRangeOfSameLengthJustBefore', () => {
    expect(getPreviousRange({ from: '2026-11-01', to: '2026-11-21' })).toEqual({
      from: '2026-10-11',
      to: '2026-10-31',
    })
  })

  it('shouldReturnYesterday_whenSingleDay', () => {
    expect(getPreviousRange({ from: '2026-03-01', to: '2026-03-01' })).toEqual({
      from: '2026-02-28',
      to: '2026-02-28',
    })
  })
})

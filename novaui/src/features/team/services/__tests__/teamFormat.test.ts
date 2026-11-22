import { formatAmount, formatArrival, formatContract, formatMonthAdvance, formatPeriod, fullName } from '../teamFormat'

const normalize = (value: string): string => value.replace(/[  ]/g, ' ')

describe('teamFormat', () => {
  it('shouldGroupThousands_andAppendAriary', () => {
    expect(normalize(formatAmount(150000))).toBe('150 000 Ar')
  })

  it.each([
    ['2021-03-15', 'mars 2021'],
    ['2024-01-01', 'janv. 2024'],
    ['2019-09-30', 'sept. 2019'],
  ])('shouldFormatArrival_%s', (date, expected) => {
    expect(formatArrival(date)).toBe(expected)
  })

  it('shouldShowDash_whenContractIsUnknown', () => {
    expect(formatContract(null)).toBe('—')
    expect(formatContract('STAGE')).toBe('Stage')
  })

  it('shouldFormatSingleDayAndRange', () => {
    expect(formatPeriod('2026-12-23', '2026-12-23')).toBe('le 23 déc.')
    expect(formatPeriod('2026-12-23', '2026-12-27')).toBe('du 23 déc. au 27 déc.')
  })

  it('shouldJoinNames', () => {
    expect(fullName({ firstName: 'Hery', lastName: 'Rabe' })).toBe('Hery Rabe')
  })

  it('shouldDescribeMonthAdvance', () => {
    expect(formatMonthAdvance(0, false)).toBe('—')
    expect(normalize(formatMonthAdvance(150000, false))).toBe('150 000 Ar')
    expect(normalize(formatMonthAdvance(80000, true))).toBe('80 000 Ar · à valider')
  })
})

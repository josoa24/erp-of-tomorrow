import { formatAriary } from '../ariary'
import { formatNif, formatStat } from '../fiscalIds'

describe('fiscalIds', () => {
  it('shouldGroupTenDigitNif', () => {
    expect(formatNif('4000123456')).toBe('4000 123 456')
  })

  it('shouldGroupSpacedNifAgain', () => {
    expect(formatNif(' 4000 123 456 ')).toBe('4000 123 456')
  })

  it('shouldKeepUnexpectedNifAsTyped', () => {
    expect(formatNif('NIF-12')).toBe('NIF-12')
  })

  it('shouldReturnNull_whenNifIsMissingOrBlank', () => {
    expect(formatNif(null)).toBeNull()
    expect(formatNif('  ')).toBeNull()
  })

  it('shouldGroupSeventeenDigitStat', () => {
    expect(formatStat('47111112019000123')).toBe('47111 11 2019 0 00123')
  })
})

describe('formatAriary', () => {
  it('shouldFormatWholeAmountInAriary', () => {
    expect(formatAriary(2_000_000)).toMatch(/^2\s000\s000 Ar$/)
  })

  it('shouldDropCents', () => {
    expect(formatAriary(1500.4)).toMatch(/^1\s500 Ar$/)
  })
})

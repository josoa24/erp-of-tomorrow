import { formatCompactAmount, formatDecimal, formatSignedPercent, pluralize } from '../format'

const normalize = (value: string) => value.replace(/\s/g, ' ')

describe('format', () => {
  it('shouldFormatCompactAmountWithCurrency', () => {
    expect(normalize(formatCompactAmount(2_840_000))).toBe('2,84 M Ar')
  })

  it('shouldFormatDecimalWithFrenchSeparator', () => {
    expect(formatDecimal(98.25)).toBe('98,3')
  })

  it('shouldPrefixPlusSign_whenPositive', () => {
    expect(normalize(formatSignedPercent(12))).toBe('+12 %')
  })

  it('shouldKeepMinusSign_whenNegative', () => {
    expect(normalize(formatSignedPercent(-5.2))).toBe('-5,2 %')
  })

  it('shouldPluralize_whenMoreThanOne', () => {
    expect(pluralize(1, 'vente', 'ventes')).toBe('1 vente')
    expect(pluralize(3, 'vente', 'ventes')).toBe('3 ventes')
  })
})

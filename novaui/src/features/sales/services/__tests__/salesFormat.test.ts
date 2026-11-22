import { formatAmount, formatChange } from '../salesFormat'

const normalize = (text: string | undefined): string | undefined => text?.replace(/\s/g, ' ')

describe('salesFormat', () => {
  it('shouldFormatFullAmountInAriary', () => {
    expect(normalize(formatAmount(1250000))).toBe('1 250 000 Ar')
  })

  it('shouldDescribePositiveChange', () => {
    expect(normalize(formatChange(18450000, 16600000, 'vs octobre'))).toBe('+11,1 % vs octobre')
  })

  it('shouldOmitChange_whenPreviousValueIsZero', () => {
    expect(formatChange(80200, 0, 'ce mois')).toBeUndefined()
  })
})

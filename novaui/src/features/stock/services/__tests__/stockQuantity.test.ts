import { formatStockQuantity } from '../stockQuantity'

describe('formatStockQuantity', () => {
  it('shouldPluralizeWordUnits_whenQuantityIsAboveOne', () => {
    expect(formatStockQuantity(40, 'sac')).toBe('40 sacs')
    expect(formatStockQuantity(3, 'paquet de 6')).toBe('3 paquets de 6')
    expect(formatStockQuantity(2, 'seau')).toBe('2 seaux')
  })

  it('shouldKeepSingular_whenQuantityIsZeroOrOne', () => {
    expect(formatStockQuantity(1, 'sac')).toBe('1 sac')
    expect(formatStockQuantity(0, 'bidon')).toBe('0 bidon')
  })

  it('shouldAbbreviatePieceUnits', () => {
    expect(formatStockQuantity(4, 'unité')).toBe('4 u.')
    expect(formatStockQuantity(220, 'u.')).toBe('220 u.')
    expect(formatStockQuantity(7, '')).toBe('7 u.')
  })

  it('shouldKeepAbbreviations_whenUnitIsShort', () => {
    expect(formatStockQuantity(25, 'kg')).toBe('25 kg')
    expect(formatStockQuantity(12, 'L')).toBe('12 L')
  })

  it('shouldGroupThousands_inFrench', () => {
    expect(formatStockQuantity(1200, 'sacs').replace(/\s/g, ' ')).toBe('1 200 sacs')
  })
})

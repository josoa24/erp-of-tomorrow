import { validateStockSearch } from '../stockSearch'

describe('validateStockSearch', () => {
  it('shouldKeepKnownFilterAndTrimmedSearch', () => {
    expect(validateStockSearch({ filtre: 'rupture', recherche: '  riz ' })).toEqual({
      filtre: 'rupture',
      recherche: 'riz',
    })
  })

  it('shouldDropUnknownFilterAndEmptySearch', () => {
    expect(validateStockSearch({ filtre: 'perdu', recherche: '   ' })).toEqual({})
  })

  it('shouldDropTous_becauseItIsTheDefault', () => {
    expect(validateStockSearch({ filtre: 'tous' })).toEqual({})
  })

  it('shouldKeepNumericSearch_asText', () => {
    expect(validateStockSearch({ recherche: 397 })).toEqual({ recherche: '397' })
  })
})

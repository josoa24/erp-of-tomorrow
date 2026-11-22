import { validateShellSearch } from '../shellSearch'

describe('validateShellSearch', () => {
  it('shouldKeepCreateRequestAndCatalogAdaptation', () => {
    expect(validateShellSearch({ creer: 'module', eclipse: 'stock.lots-peremption' })).toEqual({
      creer: 'module',
      eclipse: 'stock.lots-peremption',
    })
  })

  it('shouldDropUnknownValues', () => {
    expect(validateShellSearch({ creer: 'autre', eclipse: 'stock.inconnue', page: 2 })).toEqual({})
  })
})

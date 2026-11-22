import { pageBounds, pageCount, toSalesListQuery, validateSalesSearch } from '../salesSearch'

describe('salesSearch', () => {
  it('shouldKeepValidParameters', () => {
    expect(validateSalesSearch({ filtre: 'payees', recherche: ' Rova ', page: 3 })).toEqual({
      filtre: 'payees',
      recherche: 'Rova',
      page: 3,
    })
  })

  it('shouldDropDefaultOrInvalidParameters', () => {
    expect(validateSalesSearch({ filtre: 'toutes', recherche: '  ', page: 1 })).toEqual({
      filtre: undefined,
      recherche: undefined,
      page: undefined,
    })
    expect(validateSalesSearch({ filtre: 'inconnu', page: 'deux' })).toEqual({
      filtre: undefined,
      recherche: undefined,
      page: undefined,
    })
  })

  it('shouldReadNumericSearchAndTextPage', () => {
    expect(validateSalesSearch({ recherche: 412, page: '2' })).toEqual({ filtre: undefined, recherche: '412', page: 2 })
  })

  it('shouldBuildZeroBasedApiQuery_fromUrlSearch', () => {
    expect(toSalesListQuery({ filtre: 'annulees', recherche: 'Soa', page: 2 }, false)).toEqual({
      paymentStatus: ['CANCELLED'],
      search: 'Soa',
      page: 1,
      size: 20,
    })
  })

  it('shouldStartAtFirstPage_whenNoPageIsGiven', () => {
    expect(toSalesListQuery({}, true)).toEqual({ search: undefined, page: 0, size: 20 })
  })

  it('shouldComputePages', () => {
    expect(pageCount(0)).toBe(1)
    expect(pageCount(41)).toBe(3)
    expect(pageBounds(3, 41)).toEqual({ first: 41, last: 41 })
    expect(pageBounds(1, 0)).toEqual({ first: 0, last: 0 })
  })
})

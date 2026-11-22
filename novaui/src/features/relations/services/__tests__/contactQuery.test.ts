import { toContactQuery, visibleFilter } from '../contactQuery'

describe('toContactQuery', () => {
  it.each([
    [undefined, { kind: 'ALL' }],
    ['clients', { kind: 'CUSTOMER' }],
    ['fournisseurs', { kind: 'SUPPLIER' }],
    ['a-relancer', { kind: 'ALL', followUp: true }],
    ['professionnels', { kind: 'ALL', professional: true }],
  ] as const)('shouldMapFilter_%s', (filter, expected) => {
    expect(toContactQuery(filter, '')).toEqual(expected)
  })

  it('shouldAddSearch_whenTextIsGiven', () => {
    expect(toContactQuery('clients', 'Antsirabe')).toEqual({ kind: 'CUSTOMER', search: 'Antsirabe' })
  })
})

describe('visibleFilter', () => {
  it('shouldFallBackToAll_whenProfessionalFilterIsUsedWithoutAdaptation', () => {
    expect(visibleFilter('professionnels', false)).toBeUndefined()
  })

  it('shouldKeepProfessionalFilter_whenAdaptationIsVisible', () => {
    expect(visibleFilter('professionnels', true)).toBe('professionnels')
  })

  it('shouldKeepBaseFilters_withoutAdaptation', () => {
    expect(visibleFilter('a-relancer', false)).toBe('a-relancer')
  })
})

import { validateRelationsSearch } from '../relationsSearch'

describe('validateRelationsSearch', () => {
  it('shouldKeepKnownFilterAndTrimmedQuery', () => {
    expect(validateRelationsSearch({ filtre: 'a-relancer', q: '  Rova ' })).toEqual({ filtre: 'a-relancer', q: 'Rova' })
  })

  it('shouldDropUnknownFilterAndBlankQuery', () => {
    expect(validateRelationsSearch({ filtre: 'archives', q: '   ' })).toEqual({})
  })

  it('shouldReadNumericQueryAsText_whenTheUrlParsedANumber', () => {
    expect(validateRelationsSearch({ q: 4000 })).toEqual({ q: '4000' })
  })

  it('shouldIgnoreNonTextQuery', () => {
    expect(validateRelationsSearch({ q: { nested: true } })).toEqual({})
  })
})

import { effectiveFilter, validateTeamSearch } from '../teamSearch'

describe('teamSearch', () => {
  it('shouldKeepKnownFilterAndView', () => {
    expect(validateTeamSearch({ filtre: 'conges', vue: 'demandes', autre: 'x' })).toEqual({
      filtre: 'conges',
      vue: 'demandes',
    })
  })

  it('shouldDropUnknownValues', () => {
    expect(validateTeamSearch({ filtre: 'retraites', vue: 'liste' })).toEqual({})
  })

  it('shouldShowEveryone_whenNoFilter', () => {
    expect(effectiveFilter(undefined, false)).toBe('tous')
  })

  it('shouldIgnoreAdvanceFilter_whenNotAdapted', () => {
    expect(effectiveFilter('avances', false)).toBe('tous')
    expect(effectiveFilter('avances', true)).toBe('avances')
  })

  it('shouldKeepStatusFilter', () => {
    expect(effectiveFilter('presents', false)).toBe('presents')
  })
})

import { toInitials } from '../initials'

describe('toInitials', () => {
  it('shouldTakeFirstAndLastWordInitials', () => {
    expect(toInitials('Hery Rabe')).toBe('HR')
    expect(toInitials('  rasoa  marie andria ')).toBe('RA')
  })

  it('shouldTakeSingleInitial_whenNameHasOneWord', () => {
    expect(toInitials('Toky')).toBe('T')
  })

  it('shouldReturnEmpty_whenNameIsBlank', () => {
    expect(toInitials('   ')).toBe('')
  })
})

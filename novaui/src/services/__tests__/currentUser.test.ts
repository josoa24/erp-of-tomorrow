import { displayNameFromEmail, toCurrentUser } from '../currentUser'

describe('displayNameFromEmail', () => {
  it('shouldCapitaliseLocalPart', () => {
    expect(displayNameFromEmail('hery@boutique-tana.mg')).toBe('Hery')
  })

  it('shouldSplitDottedNames', () => {
    expect(displayNameFromEmail('rasoa.andria@boutique-tana.mg')).toBe('Rasoa Andria')
    expect(displayNameFromEmail('TOKY_RAKOTO@erp.local')).toBe('Toky Rakoto')
  })

  it('shouldFallBackToEmail_whenLocalPartIsEmpty', () => {
    expect(displayNameFromEmail('@erp.local')).toBe('@erp.local')
  })
})

describe('toCurrentUser', () => {
  it('shouldDeriveIdentityFromSession', () => {
    expect(toCurrentUser({ email: 'hery.rabe@boutique-tana.mg', role: 'ADMIN' })).toEqual({
      email: 'hery.rabe@boutique-tana.mg',
      role: 'ADMIN',
      roles: ['ADMIN'],
      roleLabel: 'Administrateur',
      roleLabels: ['Administrateur'],
      displayName: 'Hery Rabe',
      initials: 'HR',
    })
  })

  it('shouldLabelHumanResourcesRole', () => {
    expect(toCurrentUser({ email: 'rh@erp.local', role: 'RH' }).roleLabel).toBe('Équipe')
  })
})

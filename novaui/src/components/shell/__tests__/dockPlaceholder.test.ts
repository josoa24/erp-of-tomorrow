import { dockPlaceholderFor, isDockHiddenFor } from '../dockPlaceholder'

describe('dockPlaceholderFor', () => {
  it('shouldUseTheRoutePlaceholder_whenDeclared', () => {
    expect(dockPlaceholderFor({ eclipsePlaceholder: 'Demandez ou adaptez « Stock »…' })).toBe(
      'Demandez ou adaptez « Stock »…',
    )
  })

  it('shouldFallBackToGenericPlaceholder', () => {
    expect(dockPlaceholderFor({})).toBe('Demandez, adaptez un écran ou créez un module…')
    expect(dockPlaceholderFor(undefined)).toBe('Demandez, adaptez un écran ou créez un module…')
  })
})

describe('isDockHiddenFor', () => {
  it('shouldHideOnlyWhenTheRouteOptsOut', () => {
    expect(isDockHiddenFor({ hideEclipseDock: true })).toBe(true)
    expect(isDockHiddenFor({ eclipsePlaceholder: 'x' })).toBe(false)
    expect(isDockHiddenFor(undefined)).toBe(false)
  })
})

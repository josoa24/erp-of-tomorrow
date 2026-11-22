import { isEditableTarget, matchesShortcut, modKeyLabel } from '../shortcut'

const keyEvent = (init: KeyboardEventInit, target: EventTarget = document.body) => {
  const event = new KeyboardEvent('keydown', init)
  Object.defineProperty(event, 'target', { value: target })
  return event
}

describe('shortcut', () => {
  it('shouldMatchCtrlK_andMetaK', () => {
    expect(matchesShortcut(keyEvent({ key: 'k', ctrlKey: true }), 'mod-k')).toBe(true)
    expect(matchesShortcut(keyEvent({ key: 'K', metaKey: true }), 'mod-k')).toBe(true)
    expect(matchesShortcut(keyEvent({ key: 'k' }), 'mod-k')).toBe(false)
  })

  it('shouldMatchSlash_outsideEditableFields', () => {
    expect(matchesShortcut(keyEvent({ key: '/' }), 'slash')).toBe(true)
  })

  it('shouldIgnoreSlash_whenTypingInAField', () => {
    const input = document.createElement('textarea')

    expect(matchesShortcut(keyEvent({ key: '/' }, input), 'slash')).toBe(false)
  })

  it('shouldDetectEditableTargets', () => {
    expect(isEditableTarget(document.createElement('input'))).toBe(true)
    expect(isEditableTarget(document.createElement('button'))).toBe(false)
    expect(isEditableTarget(null)).toBe(false)
  })

  it('shouldLabelModifierForNonApplePlatforms', () => {
    vi.spyOn(navigator, 'platform', 'get').mockReturnValue('Linux x86_64')

    expect(modKeyLabel()).toBe('Ctrl K')
  })

  it('shouldLabelModifierForApplePlatforms', () => {
    vi.spyOn(navigator, 'platform', 'get').mockReturnValue('MacIntel')

    expect(modKeyLabel()).toBe('⌘ K')
  })
})

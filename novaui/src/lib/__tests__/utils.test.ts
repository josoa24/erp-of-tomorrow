import { cn } from '../utils'

describe('cn', () => {
  it('shouldMergeConflictingTailwindClasses', () => {
    expect(cn('px-2', 'px-4')).toBe('px-4')
  })

  it('shouldIgnoreFalsyValues', () => {
    expect(cn('a', false, undefined, 'b')).toBe('a b')
  })

  it('shouldNotMixNovaTextStyleWithInkColor', () => {
    expect(cn('text-body-s', 'text-ink-primary')).toBe('text-body-s text-ink-primary')
  })

  it('shouldKeepLastNovaTextStyle_whenTwoConflict', () => {
    expect(cn('text-body', 'text-label')).toBe('text-label')
  })

  it('shouldKeepLastNovaShadow_whenTwoConflict', () => {
    expect(cn('shadow-elevation-1', 'shadow-elevation-2')).toBe('shadow-elevation-2')
  })
})

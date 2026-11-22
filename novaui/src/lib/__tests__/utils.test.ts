import { cn } from '../utils'

describe('cn', () => {
  it('shouldMergeConflictingTailwindClasses', () => {
    expect(cn('px-2', 'px-4')).toBe('px-4')
  })

  it('shouldIgnoreFalsyValues', () => {
    expect(cn('a', false, undefined, 'b')).toBe('a b')
  })

  it('shouldNotMixDesignSystemFontSizeWithColor', () => {
    expect(cn('text-body-m', 'text-text-primary')).toBe('text-body-m text-text-primary')
  })
})

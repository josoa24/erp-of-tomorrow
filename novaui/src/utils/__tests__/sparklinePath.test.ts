import { buildSparklinePath } from '../sparklinePath'

describe('buildSparklinePath', () => {
  it('shouldReturnEmptyPath_whenNoValues', () => {
    expect(buildSparklinePath([], 100, 50)).toBe('')
  })

  it('shouldDrawFromBottomLeftToTopRight_whenIncreasing', () => {
    expect(buildSparklinePath([0, 10], 100, 50, 0)).toBe('M0.0 50.0 L100.0 0.0')
  })

  it('shouldDrawFlatLine_whenSingleValue', () => {
    expect(buildSparklinePath([5], 100, 50, 0)).toBe('M0.0 50.0 L100.0 50.0')
  })
})

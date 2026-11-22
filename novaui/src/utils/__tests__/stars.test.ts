import { generateStars } from '../stars'

describe('generateStars', () => {
  it('shouldGenerateRequestedCount_withinPercentBounds', () => {
    const stars = generateStars(30, 3)

    expect(stars).toHaveLength(30)
    expect(stars.every((s) => s.x >= 0 && s.x <= 100 && s.y >= 0 && s.y <= 100)).toBe(true)
  })

  it('shouldBeDeterministic_whenSameSeed', () => {
    expect(generateStars(5, 9)).toEqual(generateStars(5, 9))
  })
})

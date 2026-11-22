import { createSeededRandom } from '../seededRandom'

describe('createSeededRandom', () => {
  it('shouldProduceSameSequence_whenSameSeed', () => {
    const first = createSeededRandom(42)
    const second = createSeededRandom(42)

    expect([first(), first(), first()]).toEqual([second(), second(), second()])
  })

  it('shouldStayBetweenZeroAndOne', () => {
    const random = createSeededRandom(7)

    const values = Array.from({ length: 200 }, random)

    expect(values.every((value) => value >= 0 && value < 1)).toBe(true)
  })
})

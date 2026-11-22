import { createSeededRandom } from './seededRandom'

export interface Star {
  x: number
  y: number
  size: number
  opacity: number
}

export const generateStars = (count: number, seed: number): Star[] => {
  const random = createSeededRandom(seed)
  return Array.from({ length: count }, () => ({
    x: random() * 100,
    y: random() * 100,
    size: 1 + random() * 2,
    opacity: 0.3 + random() * 0.7,
  }))
}

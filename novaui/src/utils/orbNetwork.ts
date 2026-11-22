import { createSeededRandom } from './seededRandom'

export interface OrbNode {
  x: number
  y: number
  r: number
}

export interface OrbLink {
  x1: number
  y1: number
  x2: number
  y2: number
}

const CENTER = 50
const RADIUS = 47

export const buildOrbNetwork = (count: number, seed: number, maxDistance = 22) => {
  const random = createSeededRandom(seed)
  const nodes: OrbNode[] = Array.from({ length: count }, () => {
    const angle = random() * Math.PI * 2
    const distance = Math.sqrt(random()) * RADIUS
    return {
      x: CENTER + Math.cos(angle) * distance,
      y: CENTER + Math.sin(angle) * distance,
      r: 0.4 + random() * 0.8,
    }
  })
  const links: OrbLink[] = []
  nodes.forEach((a, i) =>
    nodes.slice(i + 1).forEach((b) => {
      if (Math.hypot(a.x - b.x, a.y - b.y) <= maxDistance) {
        links.push({ x1: a.x, y1: a.y, x2: b.x, y2: b.y })
      }
    }),
  )
  return { nodes, links }
}

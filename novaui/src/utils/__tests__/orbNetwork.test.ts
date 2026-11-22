import { buildOrbNetwork } from '../orbNetwork'

describe('buildOrbNetwork', () => {
  it('shouldPlaceEveryNodeInsideTheOrb', () => {
    const { nodes } = buildOrbNetwork(40, 5)

    expect(nodes).toHaveLength(40)
    expect(nodes.every((n) => Math.hypot(n.x - 50, n.y - 50) <= 47)).toBe(true)
  })

  it('shouldOnlyLinkCloseNodes', () => {
    const { links } = buildOrbNetwork(40, 5, 15)

    expect(links.every((l) => Math.hypot(l.x1 - l.x2, l.y1 - l.y2) <= 15)).toBe(true)
  })

  it('shouldHaveNoLinks_whenDistanceIsZero', () => {
    expect(buildOrbNetwork(10, 1, 0).links).toHaveLength(0)
  })
})

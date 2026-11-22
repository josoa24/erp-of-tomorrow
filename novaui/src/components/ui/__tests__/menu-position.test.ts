import { computeMenuPosition } from '../menu-position'

const viewport = { width: 1440, height: 900 }
const rect = (top: number, left: number, size = 44) => ({ top, bottom: top + size, left, right: left + size })

describe('computeMenuPosition', () => {
  it('shouldOpenBelowAndAlignRightEdges_whenThereIsRoom', () => {
    expect(computeMenuPosition(rect(23, 1352, 56), 220, viewport, 'end')).toEqual({ top: 87, right: 32 })
  })

  it('shouldOpenAbove_whenTheMenuWouldOverflowTheBottom', () => {
    expect(computeMenuPosition(rect(800, 100), 200, viewport, 'start')).toEqual({ bottom: 108, left: 100 })
  })

  it('shouldKeepAViewportMargin_onSmallScreens', () => {
    expect(computeMenuPosition(rect(10, 4), 100, { width: 375, height: 700 }, 'start')).toEqual({ top: 62, left: 16 })
  })
})

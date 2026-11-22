import { act, renderHook } from '@testing-library/react'
import { useBreakpoint } from '../useBreakpoint'
import { useMediaQuery } from '../useMediaQuery'
import { mockViewport } from '../../test/mockViewport'

describe('useBreakpoint', () => {
  afterEach(() => vi.unstubAllGlobals())

  it.each([
    [375, 'mobile'],
    [768, 'tablet'],
    [1279, 'tablet'],
    [1440, 'desktop'],
  ])('shouldReturnBreakpointForWidth_%s', (width, expected) => {
    mockViewport(width)

    expect(renderHook(() => useBreakpoint()).result.current).toBe(expected)
  })

  it('shouldFollowViewportChanges', () => {
    const viewport = mockViewport(1440)
    const { result } = renderHook(() => useBreakpoint())

    act(() => viewport.resize(375))

    expect(result.current).toBe('mobile')
  })

  it('shouldUseFallback_whenMatchMediaIsMissing', () => {
    vi.stubGlobal('matchMedia', undefined)

    expect(renderHook(() => useMediaQuery('(min-width: 768px)', true)).result.current).toBe(true)
  })
})

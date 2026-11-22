import { renderHook } from '@testing-library/react'
import { useDebouncedCallback } from '../useDebouncedCallback'

describe('useDebouncedCallback', () => {
  beforeEach(() => vi.useFakeTimers())
  afterEach(() => vi.useRealTimers())

  it('shouldCallLatestArguments_onceAfterDelay', () => {
    const callback = vi.fn()
    const { result } = renderHook(() => useDebouncedCallback(callback, 250))

    result.current('ri')
    result.current('riz')
    vi.advanceTimersByTime(250)

    expect(callback).toHaveBeenCalledTimes(1)
    expect(callback).toHaveBeenCalledWith('riz')
  })

  it('shouldNotCall_afterUnmount', () => {
    const callback = vi.fn()
    const { result, unmount } = renderHook(() => useDebouncedCallback(callback, 250))

    result.current('sucre')
    unmount()
    vi.advanceTimersByTime(250)

    expect(callback).not.toHaveBeenCalled()
  })
})

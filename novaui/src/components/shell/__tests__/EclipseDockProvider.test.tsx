import { act, renderHook } from '@testing-library/react'
import type { ReactNode } from 'react'
import { EclipseDockProvider } from '../EclipseDockProvider'
import { useEclipseDock } from '../eclipse-dock-context'

const wrapper = ({ children }: { children: ReactNode }) => <EclipseDockProvider>{children}</EclipseDockProvider>

describe('EclipseDockProvider', () => {
  it('shouldOpenOnMobile_andRaiseFocusSignal', () => {
    const { result } = renderHook(() => useEclipseDock(), { wrapper })

    act(() => result.current.openDock())

    expect(result.current.mobileOpen).toBe(true)
    expect(result.current.focusSignal).toBe(1)
    act(() => result.current.closeDock())
    expect(result.current.mobileOpen).toBe(false)
  })

  it('shouldStayHidden_untilEveryHiderReleases', () => {
    const { result } = renderHook(() => useEclipseDock(), { wrapper })
    let releaseFirst = () => undefined as void
    let releaseSecond = () => undefined as void

    act(() => {
      releaseFirst = result.current.hideDock()
      releaseSecond = result.current.hideDock()
    })
    act(() => releaseFirst())
    expect(result.current.hidden).toBe(true)
    act(() => releaseSecond())

    expect(result.current.hidden).toBe(false)
  })
})

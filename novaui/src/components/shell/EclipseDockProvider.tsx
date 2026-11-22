import { useCallback, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { EclipseDockContext } from './eclipse-dock-context'

export const EclipseDockProvider = ({ children }: { children: ReactNode }) => {
  const [hiders, setHiders] = useState(0)
  const [mobileOpen, setMobileOpen] = useState(false)
  const [focusSignal, setFocusSignal] = useState(0)

  const hideDock = useCallback(() => {
    setHiders((count) => count + 1)
    return () => setHiders((count) => count - 1)
  }, [])
  const openDock = useCallback(() => {
    setMobileOpen(true)
    setFocusSignal((signal) => signal + 1)
  }, [])
  const closeDock = useCallback(() => setMobileOpen(false), [])

  const value = useMemo(
    () => ({ hidden: hiders > 0, hideDock, mobileOpen, openDock, closeDock, focusSignal }),
    [hiders, hideDock, mobileOpen, openDock, closeDock, focusSignal],
  )

  return <EclipseDockContext.Provider value={value}>{children}</EclipseDockContext.Provider>
}

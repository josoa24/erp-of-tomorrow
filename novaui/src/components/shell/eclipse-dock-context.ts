import { createContext, useContext, useEffect } from 'react'

export interface EclipseDockContextValue {
  hidden: boolean
  hideDock: () => () => void
  mobileOpen: boolean
  openDock: () => void
  closeDock: () => void
  focusSignal: number
}

const noop = () => undefined

export const EclipseDockContext = createContext<EclipseDockContextValue>({
  hidden: false,
  hideDock: () => noop,
  mobileOpen: false,
  openDock: noop,
  closeDock: noop,
  focusSignal: 0,
})

export const useEclipseDock = (): EclipseDockContextValue => useContext(EclipseDockContext)

export const useHideEclipseDock = (active = true) => {
  const { hideDock } = useEclipseDock()
  useEffect(() => (active ? hideDock() : undefined), [active, hideDock])
}

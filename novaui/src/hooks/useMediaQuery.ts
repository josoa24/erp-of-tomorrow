import { useCallback, useSyncExternalStore } from 'react'

const canMatch = (): boolean => typeof window !== 'undefined' && typeof window.matchMedia === 'function'

export const useMediaQuery = (query: string, fallback = false): boolean => {
  const subscribe = useCallback(
    (onChange: () => void) => {
      if (!canMatch()) return () => undefined
      const list = window.matchMedia(query)
      list.addEventListener('change', onChange)
      return () => list.removeEventListener('change', onChange)
    },
    [query],
  )
  const read = () => (canMatch() ? window.matchMedia(query).matches : fallback)
  return useSyncExternalStore(subscribe, read, () => fallback)
}

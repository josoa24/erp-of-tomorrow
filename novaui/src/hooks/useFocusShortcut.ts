import { useEffect } from 'react'
import type { RefObject } from 'react'
import { matchesShortcut } from '../utils/shortcut'
import type { FocusShortcut } from '../utils/shortcut'

export const useFocusShortcut = (
  targetRef: RefObject<HTMLElement | null>,
  shortcut: FocusShortcut,
  enabled = true,
) => {
  useEffect(() => {
    if (!enabled) return
    const onKeyDown = (event: KeyboardEvent) => {
      if (!matchesShortcut(event, shortcut) || !targetRef.current) return
      event.preventDefault()
      targetRef.current.focus()
    }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [targetRef, shortcut, enabled])
}

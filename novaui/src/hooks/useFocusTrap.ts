import { useEffect } from 'react'
import type { RefObject } from 'react'

const FOCUSABLE = [
  'a[href]',
  'button:not([disabled])',
  'input:not([disabled])',
  'select:not([disabled])',
  'textarea:not([disabled])',
  '[tabindex]:not([tabindex="-1"])',
].join(',')

export const focusableWithin = (container: HTMLElement): HTMLElement[] =>
  Array.from(container.querySelectorAll<HTMLElement>(FOCUSABLE))

export const useFocusTrap = (containerRef: RefObject<HTMLElement | null>, active: boolean) => {
  useEffect(() => {
    const container = containerRef.current
    if (!active || !container) return
    const previous = document.activeElement instanceof HTMLElement ? document.activeElement : null
    const [first] = focusableWithin(container)
    ;(first ?? container).focus()

    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key !== 'Tab') return
      const items = focusableWithin(container)
      if (items.length === 0) return event.preventDefault()
      const head = items[0]
      const tail = items[items.length - 1]
      if (event.shiftKey && document.activeElement === head) {
        event.preventDefault()
        tail.focus()
      } else if (!event.shiftKey && document.activeElement === tail) {
        event.preventDefault()
        head.focus()
      }
    }
    container.addEventListener('keydown', onKeyDown)
    return () => {
      container.removeEventListener('keydown', onKeyDown)
      previous?.focus()
    }
  }, [containerRef, active])
}

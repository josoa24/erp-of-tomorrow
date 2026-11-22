import type { KeyboardEvent } from 'react'

export const useEclipseBarKeys = (submit: () => void) => (event: KeyboardEvent<HTMLTextAreaElement>) => {
  if (event.key === 'Escape') {
    event.currentTarget.blur()
    return
  }
  if (event.key !== 'Enter' || event.shiftKey || event.nativeEvent.isComposing) return
  event.preventDefault()
  submit()
}

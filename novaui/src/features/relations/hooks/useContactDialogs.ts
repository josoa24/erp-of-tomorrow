import { useCallback, useState } from 'react'
import type { Contact } from '../types/contact'

export type ContactDialog =
  | { type: 'create' }
  | { type: 'interaction'; contact: Contact }
  | { type: 'timeline'; contact: Contact }

export const useContactDialogs = () => {
  const [dialog, setDialog] = useState<ContactDialog | null>(null)

  const openCreate = useCallback(() => setDialog({ type: 'create' }), [])
  const openInteraction = useCallback((contact: Contact) => setDialog({ type: 'interaction', contact }), [])
  const openTimeline = useCallback((contact: Contact) => setDialog({ type: 'timeline', contact }), [])
  const close = useCallback(() => setDialog(null), [])

  return { dialog, openCreate, openInteraction, openTimeline, close }
}

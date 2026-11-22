import { History, MessageSquarePlus } from 'lucide-react'
import type { RowAction } from '../../../components/module/types'
import type { Contact } from '../types/contact'

export const contactActions =
  (onRecord: (contact: Contact) => void, onTimeline: (contact: Contact) => void) =>
  (contact: Contact): RowAction[] => [
    { key: 'record', label: 'Enregistrer un échange', icon: MessageSquarePlus, onSelect: () => onRecord(contact) },
    { key: 'timeline', label: 'Voir la chronologie', icon: History, onSelect: () => onTimeline(contact) },
  ]

export const contactKey = (contact: Contact): string => `${contact.kind}-${contact.partyId}`

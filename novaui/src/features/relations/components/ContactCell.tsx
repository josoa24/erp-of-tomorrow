import { Avatar } from '../../../components/ui/avatar'
import type { Contact, PartyType } from '../types/contact'

const AVATAR_TONES: Record<PartyType, 'lilac' | 'mint'> = { CUSTOMER: 'lilac', SUPPLIER: 'mint' }

export const ContactCell = ({ contact }: { contact: Contact }) => (
  <span className="flex min-w-0 items-center gap-2.5">
    <Avatar name={contact.name} initials={contact.initials} tone={AVATAR_TONES[contact.kind]} decorative />
    <span className="flex min-w-0 flex-col whitespace-normal">
      <span className="text-label break-words text-ink-primary">{contact.name}</span>
      <span className="text-micro text-ink-tertiary">{contact.typeLabel}</span>
    </span>
  </span>
)
